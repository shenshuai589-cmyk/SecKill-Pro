package com.seckillpro.vo;

import lombok.Data;

/**
 * 库存监控接口
 */
@Data
public class StockMonitorVO {

    private Long goodsId;

    private Integer redisStock;

    private Integer mysqlStock;

    private Integer totalStock;

    private Integer soldCount;

    private Boolean isConsistent;


}
