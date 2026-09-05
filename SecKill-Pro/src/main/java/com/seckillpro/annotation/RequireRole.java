package com.seckillpro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个自定义注解，
 * 之后可以贴在Controller方法上，
 * 比如 @RequireRole("ADMIN")，
 * 表示"这个接口只有ADMIN角色能调用"。
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String value();
}
