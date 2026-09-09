package com.seckillpro.controller;

import com.seckillpro.dto.PageResult;
import com.seckillpro.dto.Result;
import com.seckillpro.pojo.SeckillOrder;
import com.seckillpro.service.SeckillOrderQueryService;
import com.seckillpro.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class SeckillOrderQueryController {

    @Autowired
    SeckillOrderQueryService seckillOrderQueryService;

    @GetMapping("/my")
    public Result<PageResult<SeckillOrder>> myOrders(@RequestParam(defaultValue = "1") Integer pageNum,
                                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        PageResult<SeckillOrder> result = seckillOrderQueryService.getMyOrders(userId, pageNum, pageSize);

        return Result.success(result);
    }


    @GetMapping("/{orderNo}")
    public Result<SeckillOrder>  orderDetail(@PathVariable String orderNo) {
        Long userId = UserContext.getUserId();
        SeckillOrder order = seckillOrderQueryService.getOrderDetail(orderNo, userId);

        return Result.success(order);
    }

}
