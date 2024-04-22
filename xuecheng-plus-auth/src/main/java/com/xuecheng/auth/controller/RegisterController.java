package com.xuecheng.auth.controller;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.model.dto.RegisterParamDto;
import com.xuecheng.ucenter.service.RegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* @description 用户注册控制器
* @author TMC
* @date 2023/2/21 15:05
* @version 1.0
*/
@Api(value = "用户注册控制器", tags = {"用户注册控制器"})
@RestController
public class RegisterController {

    @Resource
    RegisterService registerService;

    @ApiOperation(value = "用户注册接口")
    @PostMapping("/register")
    public RestResponse<String> register(@RequestBody @Validated RegisterParamDto registerParamDto) {

        return registerService.register(registerParamDto);

    }
}
