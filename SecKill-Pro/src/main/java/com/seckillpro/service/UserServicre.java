package com.seckillpro.service;

import com.seckillpro.dto.LoginDTO;
import com.seckillpro.dto.RegisterDTO;
import com.seckillpro.vo.LoginVO;

public interface UserServicre {
//    注册
    void register(RegisterDTO dto);

    //登录
    LoginVO  login(LoginDTO dto);
}
