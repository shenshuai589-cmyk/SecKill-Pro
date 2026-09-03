package com.seckillpro.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SeckillGoods {

    private Long id; //商品编号
    private String goodsName; // 商品名称
    private String goodsImg; // 商品图片
    private String goodsDetail; // 商品细节
    private BigDecimal originalPrice; //商品原始价格
    private BigDecimal seckillPrice; // 秒杀价（比原价低，吸引用户抢购）
    private Integer stock; // 总库存
    private Integer stockCount; // 剩余库存

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime; //秒杀活动开始时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime; //秒杀活动结束时间

    private Integer status; // 商品当前状态：0=还没开始，1=正在秒杀中，2=已经结束，3=管理员手动下架

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; //创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime; // 最后一次被修改的时间
}
