package com.seckillpro.service;

import com.seckillpro.dto.PageResult;
import com.seckillpro.pojo.SeckillGoods;
import com.seckillpro.vo.StockMonitorVO;
import org.apache.ibatis.annotations.Param;


public interface SeckillGoodsService {

    // 发布秒杀商品
    Long createGoods(SeckillGoods goods);


    // 查询商品详情
    SeckillGoods getGoodsDetail(@Param("id") Long id, @Param("userId") Long userId);

    // 分页查询商品列表
    PageResult<SeckillGoods> getGoodsList(@Param("status") Integer status,
                                          @Param("pageNum") Integer pageNum,
                                          @Param("pageSize") Integer pageSize);

    // 编辑商品
    void updateGoods(SeckillGoods goods);

    // 下架商品
    void removeGoods(Long id);

    //库存监控
    StockMonitorVO getStockMonitor(Long goodsId);

}
