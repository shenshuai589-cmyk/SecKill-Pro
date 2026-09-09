package com.seckillpro.controller;

import com.seckillpro.annotation.RequireRole;
import com.seckillpro.dto.Result;
import com.seckillpro.service.SeckillGoodsService;
import com.seckillpro.vo.StockMonitorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/seckill")
public class AdminSeckillGoodsController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @RequireRole("ADMIN")
    @GetMapping("/stock/{goodsId}")
    public Result<StockMonitorVO> stockMonitor(@PathVariable Long goodsId){
        StockMonitorVO vo = seckillGoodsService.getStockMonitor(goodsId);
        return Result.success(vo);
    }
}
