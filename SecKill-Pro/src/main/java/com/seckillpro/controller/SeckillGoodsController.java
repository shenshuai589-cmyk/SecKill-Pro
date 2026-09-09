package com.seckillpro.controller;

import com.seckillpro.annotation.RequireRole;
import com.seckillpro.dto.PageResult;
import com.seckillpro.dto.Result;
import com.seckillpro.pojo.SeckillGoods;
import com.seckillpro.service.SeckillGoodsService;
import com.seckillpro.utils.JwtUtil;
import com.seckillpro.vo.StockMonitorVO;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seckill/goods")
public class SeckillGoodsController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private JwtUtil jwtUtil;

    // 商品列表
    @GetMapping("/list")
    public Result<PageResult<SeckillGoods>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize){
        PageResult<SeckillGoods> result = seckillGoodsService.getGoodsList(status, pageNum, pageSize);
        return Result.success(result);
    }

    // 商品详情（游客也能访问，不强制登录，见WebConfig的拦截器排除名单）
    @GetMapping("/{id}")
    public Result<SeckillGoods> detail(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String authHeader){
        // 这个接口对游客开放，所以JwtInterceptor不会给我们填充UserContext；
        // 这里手动尝试解析一下token（仅用于判断hasParticipated），解析失败就当匿名用户处理，不报错
        Long userId = tryResolveUserId(authHeader);
        SeckillGoods goods = seckillGoodsService.getGoodsDetail(id, userId);
        return Result.success(goods);
    }

    private Long tryResolveUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        try {
            Claims claims = jwtUtil.parseToken(authHeader.substring(7));
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            return null;
        }
    }
}
