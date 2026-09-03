package com.seckillpro.controller;

import com.seckillpro.dto.PageResult;
import com.seckillpro.dto.Result;
import com.seckillpro.pojo.SeckillGoods;
import com.seckillpro.service.SeckillGoodsService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seckill/goods")
public class SeckillGoodsController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    // 商品列表
    @GetMapping("/list")
    public Result<PageResult<SeckillGoods>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize){
        PageResult<SeckillGoods> result = seckillGoodsService.getGoodsList(status, pageNum, pageSize);
        return Result.success(result);
    }

    // 商品详情
    @GetMapping("/{id}")
    public Result<SeckillGoods> detail(@PathVariable Long id){
        SeckillGoods goods = seckillGoodsService.getGoodsDetail(id);
        return Result.success(goods);
    }
}
