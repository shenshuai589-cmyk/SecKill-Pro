package com.seckillpro;

import com.seckillpro.controller.SeckillOrderController;
import com.seckillpro.dto.Result;
import com.seckillpro.pojo.SeckillGoods;
import com.seckillpro.service.SeckillGoodsService;
import com.seckillpro.utils.UserContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SeckillOrderControllerTest {

    @Autowired
    private SeckillOrderController seckillOrderController;

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Test
    public void testSeckillSuccess() throws InterruptedException {
        Long goodsId = createTestGoods(10);

        UserContext.setUserId(9001L);
        Result<String> result = seckillOrderController.doSeckill(goodsId);
        UserContext.clear();

        System.out.println("场景1-秒杀成功: code=" + result.getCode() + ", message=" + result.getMessage());
        assertEquals(200, result.getCode());

        Thread.sleep(1000); // 等待消费者异步处理完成
    }

    @Test
    public void testSeckillDuplicate() throws InterruptedException {
        Long goodsId = createTestGoods(10);

        UserContext.setUserId(9002L);
        seckillOrderController.doSeckill(goodsId);
        Result<String> result = seckillOrderController.doSeckill(goodsId);
        UserContext.clear();

        System.out.println("场景2-重复参与: code=" + result.getCode() + ", message=" + result.getMessage());
        assertEquals(409, result.getCode());

        Thread.sleep(1000);
    }

    @Test
    public void testSeckillSoldOut() throws InterruptedException {
        Long goodsId = createTestGoods(1);

        UserContext.setUserId(9003L);
        seckillOrderController.doSeckill(goodsId);
        UserContext.clear();

        UserContext.setUserId(9004L);
        Result<String> result = seckillOrderController.doSeckill(goodsId);
        UserContext.clear();

        System.out.println("场景3-库存不足: code=" + result.getCode() + ", message=" + result.getMessage());
        assertEquals(409, result.getCode());

        Thread.sleep(1000);
    }

    private Long createTestGoods(int stock) {
        SeckillGoods goods = new SeckillGoods();
        goods.setGoodsName("测试商品-自动化测试");
        goods.setGoodsImg("https://via.placeholder.com/300");
        goods.setGoodsDetail("单元测试专用");
        goods.setOriginalPrice(new BigDecimal("100"));
        goods.setSeckillPrice(new BigDecimal("50"));
        goods.setStock(stock);
        goods.setStartTime(LocalDateTime.now());
        goods.setEndTime(LocalDateTime.now().plusDays(30));
        return seckillGoodsService.createGoods(goods);
    }
}