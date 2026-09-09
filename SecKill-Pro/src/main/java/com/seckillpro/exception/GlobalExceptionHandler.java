package com.seckillpro.exception;

import com.seckillpro.dto.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理：把散落在各层的异常统一"翻译"成文档里约定的 {code, message, data} 格式，
 * 避免前端拿到Spring默认的Whitelabel错误页或者不认识的JSON结构。
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    // service层主动抛的业务异常（商品不存在、无权限、库存不足……），code由抛出方决定
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.fail(e.getCode(),e.getMessage());
    }

    // @RequestBody 用@Valid校验失败时抛这个，取第一条校验错误信息返回
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK) // Result里已经有code字段表达状态了，HTTP状态码统一200，由前端读body里的code判断
    public Result<Void> handleValidException(MethodArgumentNotValidException e){

        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + err.getDefaultMessage())
                .orElse("参数错误");
        return Result.fail(400,message);
    }

    // 请求体JSON格式不对、字段类型对不上（比如id传了个字符串）
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public Result<Void> handleParamException(Exception e) {
        return Result.fail(400,"参数错误");
    }

    // 兜底：没被上面几个分支捕获的所有异常，一律当成500系统繁忙处理，避免把内部异常堆栈直接暴露给前端

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        e.printStackTrace();
        return Result.fail(500,"系统繁忙，请稍后重试");
    }





}
