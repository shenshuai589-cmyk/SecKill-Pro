package com.seckillpro.service.impl;

import com.seckillpro.dto.SeckillMessage;
import com.seckillpro.mapper.SeckillGoodsMapper;
import com.seckillpro.mapper.SeckillOrderMapper;
import com.seckillpro.pojo.SeckillGoods;
import com.seckillpro.pojo.SeckillOrder;
import com.seckillpro.service.SeckillOrderProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SeckillOrderProcessServiceImpl implements SeckillOrderProcessService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Override
    public void processOrder(SeckillMessage message) {
        Long userId = message.getUserId();
        Long goodsId = message.getGoodsId();


        // 防止消息重复消费导致重复下单（双重保险，Redis Lua脚本层面已经拦过一次了）
        SeckillOrder existing = seckillOrderMapper.selectByUserIdAndGoodsId(userId, goodsId);
        if (existing != null) {
            System.out.println("订单已存在，跳过处理,userId=" + userId + ",goodsId:" + goodsId);

            return;
        }

        // 查商品信息，冗余存一份goodsName和价格到订单里
        SeckillGoods goods = seckillGoodsMapper.selectById(goodsId);
        if (goods == null) {
            System.out.println("商品不存在，跳过处理,goodsId=" + goodsId);
            return;
        }

        // 数据库层面再次扣减库存（第二道防线，防止Redis和MySQL数据不一致）
        int rows = seckillGoodsMapper.deductStock(goodsId);
        if(rows ==0){
            System.out.println("库存不足，扣减失败,goodsId=" + goodsId);
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
    }

    private String generateOrderNo() {
        return "SK"  + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0,6).toUpperCase();
    }


}
