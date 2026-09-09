package com.seckillpro.service.impl;

import com.seckillpro.config.RabbitMQConfig;
import com.seckillpro.dto.SeckillMessage;
import com.seckillpro.service.SeckillOrderService;
import com.seckillpro.utils.RedisKeys;
import com.seckillpro.vo.SeckillResultVO;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RMap;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Arrays;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private String seckillScript;

    private static final String STOCK_KEY_PREFIX = RedisKeys.STOCK_KEY_PREFIX;
    private static final String USERS_KEY_PREFIX = RedisKeys.USERS_KEY_PREFIX;

    // 结果状态在Redis中保留多久，超过这个时间前端就查不到了（正常情况下MQ几秒内就处理完了，30分钟足够兜底）
    private static final long RESULT_TTL_MINUTES = 30;

    // 项目启动时，把Lua脚本文件的内容读进内存，避免每次请求都读一次文件
    @PostConstruct
    public void init() throws IOException {
        ClassPathResource resource = new ClassPathResource("lua/seckill.lua");
        seckillScript = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
    }

    @Override
    public int doSeckill(Long goodsId, Long userId) {
        String stockKey = STOCK_KEY_PREFIX + goodsId;
        String usersKey = USERS_KEY_PREFIX + goodsId;

        Long result = redissonClient.getScript(StringCodec.INSTANCE)
                .eval(
                        RScript.Mode.READ_WRITE,
                        seckillScript,
                        RScript.ReturnType.INTEGER,
                        Arrays.asList(stockKey, usersKey),
                        String.valueOf(userId)
                );
        int resultCode = result.intValue();

        if(resultCode == 1){
            // 库存预扣成功：先把状态标记为PENDING，前端马上就能轮询到"处理中"，
            // 再把消息扔进MQ交给消费者异步落库，避免同步写库拖慢响应
            RMap<String, String> resultMap = redissonClient.getMap(RedisKeys.resultKey(goodsId, userId), StringCodec.INSTANCE);
            resultMap.put("status", "PENDING");
            resultMap.expire(Duration.ofMinutes(RESULT_TTL_MINUTES));

            SeckillMessage message = new SeckillMessage(userId, goodsId);
            rabbitTemplate.convertAndSend(RabbitMQConfig.SECKILL_ORDER_QUEUE, message);
        }
        return result.intValue();
    }

    @Override
    public SeckillResultVO getResult(Long goodsId, Long userId) {
        RMap<String, String> resultMap = redissonClient.getMap(RedisKeys.resultKey(goodsId, userId), StringCodec.INSTANCE);

        String status = resultMap.get("status");
        if (status == null) {
            // Redis里查不到记录：要么用户压根没参与过这次秒杀，要么记录已过期
            return new SeckillResultVO("FAILED", null, null);
        }

        String orderNo = resultMap.get("orderNo");
        String orderIdStr = resultMap.get("orderId");
        Long orderId = orderIdStr != null ? Long.valueOf(orderIdStr) : null;

        return new SeckillResultVO(status, orderNo, orderId);
    }
}