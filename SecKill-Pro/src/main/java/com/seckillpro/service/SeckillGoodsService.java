package com.seckillpro.service;

import com.seckillpro.dto.PageResult;
import com.seckillpro.pojo.SeckillGoods;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;


public interface SeckillGoodsService {

    // 发布秒杀商品
    Long createGoods(SeckillGoods goods);


    // 查询商品详情
    SeckillGoods getGoodsDetail(@Param("id") Long id);

    // 分页查询商品列表
    PageResult<SeckillGoods> getGoodsList(@Param("status") Integer status,
                                          @Param("pageNum") Integer pageNum,
                                          @Param("pageSize") Integer pageSize);

    // 编辑商品
    void updateGoods(SeckillGoods goods);

    // 下架商品
    void removeGoods(Long id);
}
