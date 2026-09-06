package com.seckillpro;

import com.seckillpro.service.SeckillOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SeckillOrderServiceTest {

    @Autowired
    private SeckillOrderService seckillOrderService;


    @Test
    public void testDoSeckill() {
        Long goodsId = 3L;
        Long userId = 1L;

        int result = seckillOrderService.doSeckill(goodsId, userId);
        System.out.println("秒杀结果：" + result);
    }
}