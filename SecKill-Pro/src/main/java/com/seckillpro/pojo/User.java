package com.seckillpro.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;  // 用户id
    private String username; // 用户名称

    @JsonIgnore // 防止查询用户信息时把密码返回给前端
    private String password; // 密码

    private String nickname; // 用户昵称

    private String avatar; //头像 / 用户头像

    private String role;  // user /admin

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
