package com.seckillpro.service;

import com.seckillpro.dto.PageResult;
import com.seckillpro.pojo.SeckillOrder;

public interface SeckillOrderQueryService {

    PageResult<SeckillOrder> getMyOrders(Long userId, Integer pageNum, Integer pageSize);

    SeckillOrder getOrderDetail(String orderNo, Long userId);
}
