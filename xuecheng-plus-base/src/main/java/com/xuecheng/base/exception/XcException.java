package com.xuecheng.base.exception;

import lombok.Getter;

/**
* @description 学成在线项目异常类
* @author TMC
* @date 2023/2/7 17:08
* @version 1.0
*/
@Getter
public class XcException extends RuntimeException{

    private String errMessage;


    public XcException() {
        super();
    }

    public XcException(String message) {
        super(message);
        this.errMessage = message;
    }

    public static void cast(CommonError commonError) {
        throw new XcException(commonError.getErrMessage());
    }

    public static void cast(String errMessage) {
        throw new XcException(errMessage);
    }

}
