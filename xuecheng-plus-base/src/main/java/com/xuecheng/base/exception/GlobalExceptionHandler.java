package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
* @description 全局异常处理器
* @author TMC
* @date 2023/2/7 17:17
* @version 1.0
*/
@Slf4j
@RestControllerAdvice //返回json格式
public class GlobalExceptionHandler {

    //1.处理项目自定义异常
    @ExceptionHandler(XcException.class) //捕获自定义异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //状态码返回500
    public RestErrorResponse handleXcException(XcException e) {
        log.error("GlobalExceptionHandler-->XcException:{}", e.getErrMessage());
        e.printStackTrace();
        return new RestErrorResponse(e.getErrMessage());
    }

    //2.处理非预期异常
    @ExceptionHandler(Exception.class) //捕获非预期异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //状态码返回500
    public RestErrorResponse handleException(Exception e) {

        log.error("GlobalExceptionHandler-->Exception:{}", e.getMessage());
        e.printStackTrace();
        return new RestErrorResponse(CommonError.UNKNOWN_ERROR.getErrMessage());

    }

    //3.处理请求体对象类参数中成员变量校验没有通过的异常
    @ExceptionHandler(value = MethodArgumentNotValidException.class) //捕获非法参数异常
    @ResponseStatus(HttpStatus.BAD_REQUEST) //状态码返回400
    public RestErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        log.error("GlobalExceptionHandler-->MethodArgumentNotValidException:{}", e);
        e.printStackTrace();
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        return new RestErrorResponse(fieldErrors.get(0).getDefaultMessage());

    }

    //4.处理查询条件对象类参数中成员变量校验没有通过的异常
    @ExceptionHandler({BindException.class}) //捕获非法参数异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)  //状态码返回400
    public RestErrorResponse handleBindException(BindException e) {

        log.warn("GlobalExceptionHandler-->BindException:{}", e);
        e.printStackTrace();
        List<ObjectError> allErrors = e.getAllErrors();
        return new RestErrorResponse(allErrors.get(0).getDefaultMessage());

    }

    //5.处理原子类型参数没有通过校验的异常
    @ExceptionHandler(value = ConstraintViolationException.class) //捕获非法参数异常
    @ResponseStatus(HttpStatus.BAD_REQUEST) //状态码返回400
    public RestErrorResponse handleConstraintViolationException(ConstraintViolationException e) {

        log.error("GlobalExceptionHandler-->ConstraintViolationException:{}", e);
        e.printStackTrace();
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        Iterator<ConstraintViolation<?>> iterator = constraintViolations.iterator();
        ConstraintViolation<?> next = iterator.next();
        return new RestErrorResponse(next.getMessage());

    }






}
