package com.xuecheng.media.config;

import com.xuecheng.base.exception.RestErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
* @description 认证授权异常处理器
* @author TMC
* @date 2023/3/7 12:07
* @version 1.0
*/
@RestControllerAdvice(basePackages = "com.xuecheng.media.api")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AuthExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class) //捕获认证授权异常
    @ResponseStatus(HttpStatus.UNAUTHORIZED) //返回响应码401
    public RestErrorResponse handleAccessDeniedException(AccessDeniedException e) {

        log.error("GlobalExceptionHandler-->AccessDeniedException:{}", e.getMessage());
        e.printStackTrace();
        return new RestErrorResponse("没有操作此功能的权限");

    }

}
