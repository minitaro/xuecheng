package com.xuecheng.ucenter.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
* @description 找回密码请求dto
* @author TMC
* @date 2023/2/21 0:09
* @version 1.0
*/
@ApiModel(value="FindPwdParamsDto", description="找回密码请求")
@Data
@ToString
public class FindPwdParamsDto {

    @ApiModelProperty(value = "确认密码", required = true)
    @NotBlank(message = "确认密码不能为空")
    private String confirmpwd;

    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank(message = "新密码不能为空")
    private String password;

    @ApiModelProperty(value = "手机号")
    private String cellphone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "验证码")
    @NotBlank(message = "验证码不能为空")
    private String checkcode;

    @ApiModelProperty(value = "验证码key")
    @NotBlank(message = "验证码key不能为空")
    private String checkcodekey;

}
