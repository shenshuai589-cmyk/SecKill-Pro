package com.seckillpro.service.impl;

import com.seckillpro.config.RabbitMQConfig;
import com.seckillpro.dto.SeckillMessage;
import com.seckillpro.service.SeckillOrderService;
import jakarta.annotation.PostConstruct;
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
import java.util.Arrays;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private String seckillScript;

    private static final String STOCK_KEY_PREFIX = "seckill:stock:";
    private static final String USERS_KEY_PREFIX = "seckill:users:";

    // 项目启动时，把Lua脚本文件的内容读进内存，避免每次请求都读一次文件
    @PostConstruct    // @PostConstruct：这是Spring提供的注解，表示"这个方法在Bean初始化完成后自动执行一次"
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
            SeckillMessage message = new SeckillMessage(userId, goodsId);

            rabbitTemplate.convertAndSend(RabbitMQConfig.SECKILL_ORDER_QUEUE, message);
        }
        return result.intValue();
    }
}
