package com.seckillpro;

import com.seckillpro.pojo.SeckillGoods;
import com.seckillpro.service.SeckillGoodsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
public class SeckillGoodsServiceTest {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Test
    public void testCreateGoods() {
        SeckillGoods goods = new SeckillGoods();
        goods.setGoodsName("测试商品-单元测试");
        goods.setGoodsImg("https://via.placeholder.com/300");
        goods.setGoodsDetail("单元测试专用商品");
        goods.setOriginalPrice(new BigDecimal("100"));
        goods.setSeckillPrice(new BigDecimal("50"));
        goods.setStock(20);
        goods.setStartTime(LocalDateTime.now());
        goods.setEndTime(LocalDateTime.now().plusDays(30));

        Long id = seckillGoodsService.createGoods(goods);

        System.out.println("创建成功，商品ID是：" + id);
    }
}