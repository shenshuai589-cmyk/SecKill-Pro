package com.seckillpro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 这个类必须 implements Serializable——
 * 因为这个对象要通过网络，从"生产者"传输到RabbitMQ，
 * 再传到"消费者"，中间必须能被序列化成字节流传输。
 */
public class SeckillMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long goodsId;
}
