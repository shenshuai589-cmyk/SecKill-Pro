package com.seckillpro.mapper;

import com.seckillpro.pojo.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeckillGoodsMapper {

    // 插入一条秒杀商品
    int insert(SeckillGoods goods);

    // 根据id查询商品详请
    SeckillGoods selectById(@Param("id") Long id);

    // 分页查询商品列表
    List<SeckillGoods> selectList(@Param("status") Integer status,
                                  @Param("offset") Integer offset, //跳过多少条
                                  @Param("pageSize") Integer pageSize); //每页返回几页

    // 查询总数，配合分页使用
    int countList(@Param("status") Integer status);

    // 更新商品信息
    int update(SeckillGoods goods);

    // 下架商品(当status=3时，逻辑删除，不是物理删除)
    int updateStatus(@Param("id") Long id,@Param("status") Integer status);

    // 扣减数据库库存（MQ消费者写库时用，带WHERE条件防止扣成负数）
    int deductStock(@Param("id") Long id);
}
