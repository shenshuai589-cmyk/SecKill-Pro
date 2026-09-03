package com.seckillpro.service.impl;

import com.seckillpro.dto.PageResult;
import com.seckillpro.mapper.SeckillGoodsMapper;
import com.seckillpro.pojo.SeckillGoods;
import com.seckillpro.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Override
    public Long createGoods(SeckillGoods goods) {
        // 新建商品时，剩余库存=总库存，默认状态未“未开始”
        goods.setStockCount(goods.getStock());
        goods.setStatus(0);
        seckillGoodsMapper.insert(goods);
        return  goods.getId(); //// insert执行后，id会自动回填到goods对象里
    }

    @Override
    public SeckillGoods getGoodsDetail(Long id) {
        SeckillGoods goods = seckillGoodsMapper.selectById(id);
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }
        return goods;
    }

    @Override
    public PageResult<SeckillGoods> getGoodsList(Integer status, Integer pageNum, Integer pageSize) {
        // 分页参数兜底，防止前端传0或负数导致SQL出错
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        int offset = (pageNum - 1) * pageSize;
        List<SeckillGoods> list = seckillGoodsMapper.selectList(status, offset, pageSize);

        int total = seckillGoodsMapper.countList(status);

        return new PageResult<>((long) total, pageNum,pageSize,list);
    }

    @Override
    public void updateGoods(SeckillGoods goods) {
        SeckillGoods existing = seckillGoodsMapper.selectById(goods.getId());
        if (existing == null) {
            throw new RuntimeException("商品不存在");
        }
        seckillGoodsMapper.update(goods);
    }

    @Override
    public void removeGoods(Long id) {
        seckillGoodsMapper.updateStatus(id, 3); // 3=管理员手动下架
    }
}
