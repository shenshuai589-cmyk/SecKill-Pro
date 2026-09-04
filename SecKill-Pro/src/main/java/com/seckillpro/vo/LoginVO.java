package com.seckillpro.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginVO {

    private String token;
    private Long userId;
    private String nickname;
    private String role;

}
