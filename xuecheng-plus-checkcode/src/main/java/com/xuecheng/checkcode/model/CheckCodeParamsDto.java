package com.xuecheng.checkcode.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
* @description 验证码请求dto
* @author TMC
* @date 2023/2/24 13:55
* @version 1.0
*/
@ApiModel(value = "CheckCodeParamsDto", description = "验证码请求")
@Data
@ToString
public class CheckCodeParamsDto {

    /**
     * 验证码类型:pic、sms、email等
     */
    @ApiModelProperty(value = "验证码类型", required = true)
    private String checkCodeType;

    /**
     * 业务携带参数
     */
    private String param1;
    private String param2;
    private String param3;

}
