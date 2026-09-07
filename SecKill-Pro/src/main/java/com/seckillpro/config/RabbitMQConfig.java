package com.seckillpro.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitMQConfig {

    public static final String SECKILL_ORDER_QUEUE = "seckill.order.queue";

    /**
     * 告诉RabbitMQ"我需要一个叫seckill.order.queue的队列，
     * 以后所有秒杀订单消息都往这里扔"。
     * true（持久化）意味着即使RabbitMQ服务重启，
     * 这个队列的定义本身不会消失
     * @return
     */

    @Bean
    public Queue seckillOrderQueue() {
        return new Queue(SECKILL_ORDER_QUEUE, true);
    }
}
