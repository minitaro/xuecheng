package com.xuecheng.base.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
* @description 通用异常信息
* @author TMC
* @date 2023/2/7 17:02
* @version 1.0
*/
@AllArgsConstructor
@Getter
public enum CommonError {
    UNKNOWN_ERROR("未知错误"),
    INVALID_PARAMS("非法参数"),
    OBJECT_NULL("对象为空"),
    QUERY_NULL("查询结果为空"),
    REQUEST_NULL("请求参数为空"),
    ;
    private String errMessage;

}
