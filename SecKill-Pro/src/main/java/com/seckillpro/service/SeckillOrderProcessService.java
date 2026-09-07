package com.seckillpro.service;

import com.seckillpro.dto.SeckillMessage;
import com.seckillpro.pojo.SeckillOrder;

public interface SeckillOrderProcessService {
    void processOrder(SeckillMessage message);
}
