package com.seckillpro.mapper;

import com.seckillpro.pojo.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SeckillOrderMapper {

    int insert(SeckillOrder order);

    SeckillOrder selectByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);
}
