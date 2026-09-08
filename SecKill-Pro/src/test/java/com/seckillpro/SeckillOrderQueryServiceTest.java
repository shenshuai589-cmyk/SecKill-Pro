package com.seckillpro;

import com.seckillpro.dto.PageResult;
import com.seckillpro.pojo.SeckillOrder;
import com.seckillpro.service.SeckillOrderQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SeckillOrderQueryServiceTest {

    @Autowired
    private SeckillOrderQueryService seckillOrderQueryService;

    @Test
    public void testGetMyOrders() {
        // 用之前测试成功下单的userId（比如9001，参考之前SeckillOrderControllerTest里成功场景用的那个）
        Long userId = 9001L;

        PageResult<SeckillOrder> result = seckillOrderQueryService.getMyOrders(userId, 1, 10);

        System.out.println("总订单数：" + result.getTotal());
        for (SeckillOrder order : result.getList()) {
            System.out.println("订单号：" + order.getOrderNo()
                    + "，商品：" + order.getGoodsName()
                    + "，价格：" + order.getSeckillPrice()
                    + "，状态：" + order.getOrderStatus());
        }
    }
}