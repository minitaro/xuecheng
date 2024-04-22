package com.xuecheng.ucenter.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
* @description 用户注册请求dto
* @author TMC
* @date 2023/2/21 15:10
* @version 1.0
*/
@ApiModel(value = "RegisterParamDto", description = "用户注册请求")
@Data
@ToString
public class RegisterParamDto {

    @ApiModelProperty(value = "手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    private String cellphone;

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @ApiModelProperty(value = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @ApiModelProperty(value = "昵称")
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "密码确认")
    @NotBlank(message = "密码确认不能为空")
    private String confirmpwd;

    @ApiModelProperty(value = "验证码key")
    @NotBlank(message = "验证码key不能为空")
    private String checkcodekey;

    @ApiModelProperty(value = "验证码")
    @NotBlank(message = "验证码不能为空")
    private String checkcode;

}
