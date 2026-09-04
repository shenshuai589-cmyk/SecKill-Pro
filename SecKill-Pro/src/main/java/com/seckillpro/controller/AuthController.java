package com.seckillpro.controller;

import com.seckillpro.dto.LoginDTO;
import com.seckillpro.dto.RegisterDTO;
import com.seckillpro.dto.Result;
import com.seckillpro.service.UserServicre;
import com.seckillpro.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserServicre  userServicre;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO dto) {
        userServicre.register(dto);
        return Result.success(null);
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        LoginVO loginVO = userServicre.login(dto);
        return Result.success(loginVO);
    }
}
