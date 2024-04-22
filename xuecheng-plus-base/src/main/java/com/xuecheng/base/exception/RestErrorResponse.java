package com.xuecheng.base.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
* @description 统一异常响应类
* @author TMC
* @date 2023/2/7 17:14
* @version 1.0
*/
@Data
@AllArgsConstructor
public class RestErrorResponse implements Serializable {

    private String errMessage;

}
