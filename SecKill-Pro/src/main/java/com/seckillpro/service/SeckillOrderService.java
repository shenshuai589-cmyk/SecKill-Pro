package com.seckillpro.service;

import com.seckillpro.vo.SeckillResultVO;

public interface SeckillOrderService {
    // 返回值：1-成功 -1-库存不足 -2-重复参与
    int doSeckill(Long goodsId, Long userId);

    // 查询秒杀结果（前端轮询），对应 GET /api/seckill/result/{goodsId}
    SeckillResultVO getResult(Long goodsId, Long userId);
}
