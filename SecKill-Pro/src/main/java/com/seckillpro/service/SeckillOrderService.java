package com.seckillpro.service;

public interface SeckillOrderService {
    // 返回值：1-成功 -1-库存不足 -2-重复参与
    int doSeckill(Long goodsId, Long userId);
}
