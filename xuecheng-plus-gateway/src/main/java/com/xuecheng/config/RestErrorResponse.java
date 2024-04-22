package com.xuecheng.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
* @description 网关错误响应参数包装
* @author TMC
* @date 2023/2/19 15:18
* @version 1.0
*/
@Data
@ToString
@AllArgsConstructor
public class RestErrorResponse implements Serializable {

    private String errMessage;


}
