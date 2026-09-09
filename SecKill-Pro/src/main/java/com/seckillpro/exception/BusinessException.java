package com.seckillpro.exception;

import lombok.Getter;

/**
 * 业务异常。service层遇到"用户不存在""无权限"这类业务性错误时抛这个，
 * 而不是裸的RuntimeException——这样GlobalExceptionHandler才能知道该返回什么code给前端，
 * 跟接口文档里的错误码表对应上。
 */

@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    // 不传code时默认当作400参数错误处理
    public BusinessException(String message) {
        this(400, message);
    }

}
