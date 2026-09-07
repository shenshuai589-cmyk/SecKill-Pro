package com.seckillpro.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SeckillOrder {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long goodsId;
    private String goodsName;
    private BigDecimal seckillPrice;
    private Integer orderStatus; // 0-待支付 1-已支付 2-已取消 3-已超时关闭

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
}
