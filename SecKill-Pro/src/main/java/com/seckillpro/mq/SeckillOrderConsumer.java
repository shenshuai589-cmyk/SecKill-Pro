package com.seckillpro.mq;

import com.seckillpro.config.RabbitMQConfig;
import com.seckillpro.dto.SeckillMessage;
import com.seckillpro.service.SeckillOrderProcessService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeckillOrderConsumer {

    @Autowired
    private SeckillOrderProcessService seckillOrderProcessService;

    @RabbitListener(queues = RabbitMQConfig.SECKILL_ORDER_QUEUE)
    public void handleSeckillOrder(SeckillMessage message) {
        System.out.println("接收到秒杀订单消息: " + message);
        seckillOrderProcessService.processOrder(message);
    }
}
