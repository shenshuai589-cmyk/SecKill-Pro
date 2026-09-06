package com.seckillpro.controller;

import com.seckillpro.dto.Result;
import com.seckillpro.service.SeckillOrderService;
import com.seckillpro.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeckillOrderController {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @PostMapping("/api/seckill/{goodsId}")
    public Result<String> doSeckill(@PathVariable Long goodsId) {
        Long userId = UserContext.getUserId();  // 从拦截器存的上下文里取出当前登录用户

        int result = seckillOrderService.doSeckill(goodsId, userId);

        if (result == 1) {
            return Result.success("秒杀成功,正在为您生成订单");
        } else if (result == -1) {
            return Result.fail(409,"该商品已售空");
        } else if (result == 2) {
            return Result.fail(409,"您已参与过本次秒杀，请勿重复提交");
        } else {
            return Result.fail(500,"系统繁忙，请稍后重试");
        }
    }
}
