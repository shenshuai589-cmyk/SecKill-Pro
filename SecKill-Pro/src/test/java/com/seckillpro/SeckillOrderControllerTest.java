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

    // 场景1：正常秒杀成功
    @Test
    public void testSeckillSuccess() {
        Long goodsId = createTestGoods(10); // 创建一个库存为10的测试商品

        UserContext.setUserId(9001L); // 模拟"当前登录用户id=9001"
        Result<String> result = seckillOrderController.doSeckill(goodsId);
        UserContext.clear();

        System.out.println("场景1-秒杀成功: code=" + result.getCode() + ", message=" + result.getMessage());
        assertEquals(200, result.getCode());
    }

    // 场景2：同一个用户重复参与
    @Test
    public void testSeckillDuplicate() {
        Long goodsId = createTestGoods(10);

        UserContext.setUserId(9002L);
        seckillOrderController.doSeckill(goodsId); // 第一次，应该成功
        Result<String> result = seckillOrderController.doSeckill(goodsId); // 第二次，同一个用户再来一次
        UserContext.clear();

        System.out.println("场景2-重复参与: code=" + result.getCode() + ", message=" + result.getMessage());
        assertEquals(409, result.getCode());
    }

    // 场景3：库存不足
    @Test
    public void testSeckillSoldOut() {
        Long goodsId = createTestGoods(1); // 只给1件库存

        UserContext.setUserId(9003L);
        seckillOrderController.doSeckill(goodsId); // 第一个人抢到，库存变成0
        UserContext.clear();

        UserContext.setUserId(9004L); // 换一个新用户
        Result<String> result = seckillOrderController.doSeckill(goodsId); // 库存已经是0了
        UserContext.clear();

        System.out.println("场景3-库存不足: code=" + result.getCode() + ", message=" + result.getMessage());
        assertEquals(409, result.getCode());
    }

    // 辅助方法：快速创建一个测试商品，返回商品id
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