package com.seckillpro.controller;

import com.seckillpro.dto.Result;
import com.seckillpro.service.SeckillOrderService;
import com.seckillpro.utils.UserContext;
import com.seckillpro.vo.SeckillResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SeckillOrderController {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @PostMapping("/api/seckill/{goodsId}")
    public Result<Map<String, String>> doSeckill(@PathVariable Long goodsId) {
        Long userId = UserContext.getUserId();  // 从拦截器存的上下文里取出当前登录用户

        int result = seckillOrderService.doSeckill(goodsId, userId);

        if (result == 1) {
            Result<Map<String, String>> res = Result.success(Map.of("queueStatus", "PENDING"));
            res.setMessage("排队中，请稍后查看订单结果");
            return res;
        } else if (result == -1) {
            return Result.fail(409,"该商品已售罄");
        } else if (result == 2) {
            return Result.fail(409,"您已参与过本次秒杀，请勿重复提交");
        } else {
            return Result.fail(500,"系统繁忙，请稍后重试");
        }
    }

    // 前端下单后轮询此接口，获取MQ异步处理后的最终结果
    @GetMapping("/api/seckill/result/{goodsId}")
    public Result<SeckillResultVO> getResult(@PathVariable Long goodsId) {
        Long userId = UserContext.getUserId();
        SeckillResultVO vo = seckillOrderService.getResult(goodsId, userId);
        return Result.success(vo);
    }
}