package com.seckillpro.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀结果查询接口（前端轮询用）
 * status: PENDING-处理中 / SUCCESS-成功 / FAILED-失败（库存不足）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillResultVO {

    private String status;

    private String orderNo;

    private Long orderId;

}
