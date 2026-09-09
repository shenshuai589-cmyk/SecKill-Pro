package com.seckillpro.service.impl;

import com.seckillpro.dto.SeckillMessage;
import com.seckillpro.mapper.SeckillGoodsMapper;
import com.seckillpro.mapper.SeckillOrderMapper;
import com.seckillpro.pojo.SeckillGoods;
import com.seckillpro.pojo.SeckillOrder;
import com.seckillpro.service.SeckillOrderProcessService;
import com.seckillpro.utils.RedisKeys;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SeckillOrderProcessServiceImpl implements SeckillOrderProcessService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void processOrder(SeckillMessage message) {
        Long userId = message.getUserId();
        Long goodsId = message.getGoodsId();

        RMap<String, String> resultMap = redissonClient.getMap(RedisKeys.resultKey(goodsId, userId), StringCodec.INSTANCE);

        // 防止消息重复消费导致重复下单（双重保险，Redis Lua脚本层面已经拦过一次了）
        SeckillOrder existing = seckillOrderMapper.selectByUserIdAndGoodsId(userId, goodsId);
        if (existing != null) {
            System.out.println("订单已存在，跳过处理,userId=" + userId + ",goodsId:" + goodsId);
            markSuccess(resultMap, existing);
            return;
        }

        // 查商品信息，冗余存一份goodsName和价格到订单里
        SeckillGoods goods = seckillGoodsMapper.selectById(goodsId);
        if (goods == null) {
            System.out.println("商品不存在，跳过处理,goodsId=" + goodsId);
            resultMap.put("status", "FAILED");
            return;
        }

        // 数据库层面再次扣减库存（第二道防线，防止Redis和MySQL数据不一致）
        int rows = seckillGoodsMapper.deductStock(goodsId);
        if(rows == 0){
            System.out.println("库存不足，扣减失败,goodsId=" + goodsId);
            resultMap.put("status", "FAILED");
            return;
        }

        // 生成订单
        SeckillOrder order = new SeckillOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setGoodsId(goodsId);
        order.setGoodsName(goods.getGoodsName());
        order.setSeckillPrice(goods.getSeckillPrice());
        order.setOrderStatus(0); // 待支付

        seckillOrderMapper.insert(order);

        System.out.println("订单创建完成,orderNo=" + order.getOrderNo());
        markSuccess(resultMap, order);
    }

    // 把订单结果写回Redis，供 GET /api/seckill/result/{goodsId} 轮询接口读取
    private void markSuccess(RMap<String, String> resultMap, SeckillOrder order) {
        resultMap.put("status", "SUCCESS");
        resultMap.put("orderNo", order.getOrderNo());
        if (order.getId() != null) {
            resultMap.put("orderId", String.valueOf(order.getId()));
        }
    }

    private String generateOrderNo() {
        return "SK"  + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0,6).toUpperCase();
    }
}