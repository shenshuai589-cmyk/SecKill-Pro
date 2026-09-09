package com.seckillpro.service.impl;

import com.seckillpro.dto.LoginDTO;
import com.seckillpro.dto.RegisterDTO;
import com.seckillpro.exception.BusinessException;
import com.seckillpro.mapper.UserMapper;
import com.seckillpro.pojo.User;
import com.seckillpro.service.UserServicre;
import com.seckillpro.utils.JwtUtil;
import com.seckillpro.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserServicre {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void register(RegisterDTO dto) {

        if (dto.getUsername() ==  null ||dto.getUsername().isBlank()) {
            throw new BusinessException(400,"用户名不能为空");
        }

        if (dto.getPassword() ==  null ||dto.getPassword().isBlank()) {
            throw new BusinessException(400,"密码不能为空");
        }

        //检查用户名是否已经存在
        User existing = userMapper.selectByUsername(dto.getUsername());
        if(existing!=null){
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // 密码加密后存储
        user.setNickname(dto.getNickname());
        user.setRole("USER"); // 设置默认角色

        userMapper.insert(user);
    }

    @Override
    public LoginVO login(LoginDTO dto) {

        if (dto.getUsername() == null || dto.getUsername().isBlank()
                || dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new BusinessException(400, "用户名和密码不能为空");
        }

        User user = userMapper.selectByUsername(dto.getUsername());
        if(user==null){
            throw new RuntimeException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(dto.getPassword(),user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        return new LoginVO(token, user.getId(), user.getNickname(), user.getRole());
    }
}
