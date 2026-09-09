package com.seckillpro.service.impl;

import com.seckillpro.dto.PageResult;
import com.seckillpro.exception.BusinessException;
import com.seckillpro.mapper.SeckillGoodsMapper;
import com.seckillpro.pojo.SeckillGoods;
import com.seckillpro.service.SeckillGoodsService;
import com.seckillpro.vo.StockMonitorVO;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedissonClient  redissonClient;

    private static final String STOCK_KEY_PREFIX = "seckill:stock:";
    private static final String USERS_KEY_PREFIX = "seckill:users:";

    @Override
    public Long createGoods(SeckillGoods goods) {
        if (goods.getGoodsName() == null || goods.getGoodsName().isBlank()) {
            throw new BusinessException(400, "商品名称不能为空");
        }
        if (goods.getStock() == null || goods.getStock() < 0) {
            throw new BusinessException(400, "库存必须为非负整数");
        }
        if (goods.getStartTime() == null || goods.getEndTime() == null
                || !goods.getEndTime().isAfter(goods.getStartTime())) {
            throw new BusinessException(400, "秒杀开始/结束时间不合法");
        }

        // 新建商品时，剩余库存=总库存，默认状态未"未开始"
        goods.setStockCount(goods.getStock());
        goods.setStatus(0);
        seckillGoodsMapper.insert(goods);

        RBucket<String> bucket = redissonClient.getBucket(STOCK_KEY_PREFIX + goods.getId(), StringCodec.INSTANCE);
        bucket.set(String.valueOf(goods.getStock()));
        //关键新增：商品创建成功后，把库存同步一份到Redis
        return  goods.getId(); //// insert执行后，id会自动回填到goods对象里
    }

    @Override
    public SeckillGoods getGoodsDetail(Long id, Long userId) {
        SeckillGoods goods = seckillGoodsMapper.selectById(id);
        if (goods == null) {
            throw new BusinessException(404, "商品不存在");
        }
        // 关键新增：详情接口的库存展示，优先从Redis读取实时库存
        RBucket<String> bucket = redissonClient.getBucket(STOCK_KEY_PREFIX + id, StringCodec.INSTANCE);

        String redisStock = bucket.get();
        if (redisStock != null) {
            goods.setStockCount(Integer.parseInt(redisStock));
        }

        // 只有登录用户才判断是否已参与过（游客userId为null，直接给false）
        if (userId != null) {
            boolean participated = redissonClient.getSet(USERS_KEY_PREFIX + id, StringCodec.INSTANCE)
                    .contains(String.valueOf(userId));
            goods.setHasParticipated(participated);
        } else {
            goods.setHasParticipated(false);
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
            throw new BusinessException(404, "商品不存在");
        }
        seckillGoodsMapper.update(goods);
    }

    @Override
    public void removeGoods(Long id) {
        seckillGoodsMapper.updateStatus(id, 3); // 3=管理员手动下架
    }

    @Override
    public StockMonitorVO getStockMonitor(Long goodsId) {

        //查询mysql
        SeckillGoods goods = seckillGoodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException(404, "商品不存在");
        }

        // 查询Redis
        RBucket<String> bucket =  redissonClient.getBucket(STOCK_KEY_PREFIX + goodsId,StringCodec.INSTANCE);

        String redisStockStr = bucket.get();

        Integer redisStock = null;

        if (redisStockStr != null) {
            redisStock = Integer.parseInt(redisStockStr);
        }

        Integer totalStock =  goods.getStock(); //获取总库存
        Integer mysqlStock = goods.getStockCount();// 获取mysql中的库存

        // 4.计算已售库存
        Integer soldCount = totalStock - mysqlStock;

        // 5. 判断 Redis 和 MySQL 是否一致
        boolean isConsistent = redisStock != null && redisStock.equals(mysqlStock);

        // 6.组装VO
        StockMonitorVO vo = new StockMonitorVO();

        vo.setGoodsId(goodsId);
        vo.setRedisStock(redisStock);
        vo.setMysqlStock(mysqlStock);
        vo.setTotalStock(totalStock);
        vo.setSoldCount(soldCount);
        vo.setIsConsistent(isConsistent);

        return vo;
    }
}