package com.seckillpro.dto;

import lombok.Data;

import java.util.List;

/**
 * 包装"列表+分页信息"，跟接口文档里 data 字段的格式对应上，之后别的分页接口（比如订单列表）也能复用它
 * @param <T>
 */
@Data
public class PageResult<T> {

    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private java.util.List<T> list;

    public PageResult(Long total,Integer pageNum, Integer pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
    }
}
