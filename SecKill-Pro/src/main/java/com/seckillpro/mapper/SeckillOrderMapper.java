package com.seckillpro.mapper;

import com.seckillpro.pojo.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeckillOrderMapper {

    int insert(SeckillOrder order);

    SeckillOrder selectByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    // 分页查询某个用户的订单列表
    List<SeckillOrder> selectByUserId(@Param("userId") Long userId,
                                      @Param("offset") Integer offset,
                                      @Param("pageSize") Integer pageSize);


    // 查询该用户订单总数
    int countByUserId(@Param("userId") Long userId);


    SeckillOrder selectByOrderNo(@Param("orderNo") String orderNo);
}
