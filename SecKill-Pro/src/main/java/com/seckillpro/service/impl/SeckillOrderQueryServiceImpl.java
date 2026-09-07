package com.seckillpro.service.impl;

import com.seckillpro.dto.PageResult;
import com.seckillpro.mapper.SeckillOrderMapper;
import com.seckillpro.pojo.SeckillOrder;
import com.seckillpro.service.SeckillOrderQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillOrderQueryServiceImpl implements SeckillOrderQueryService {

    @Autowired
    private SeckillOrderMapper  seckillOrderMapper;


    @Override
    public PageResult<SeckillOrder> getMyOrders(Long userId, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize <0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        int offset = (pageNum - 1) * pageSize;

        List<SeckillOrder> list = seckillOrderMapper.selectByUserId(userId, offset, pageSize);

        int total = seckillOrderMapper.countByUserId(userId);

        return new PageResult<>((long) total, pageNum, pageSize, list);
    }

    @Override
    public SeckillOrder getOrderDetail(String orderNo, Long userId) {
        SeckillOrder order = seckillOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看该订单");
        }
        return order;
    }
}
