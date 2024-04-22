package com.xuecheng.auth.controller;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.model.dto.FindPwdParamsDto;
import com.xuecheng.ucenter.service.FindPasswordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description 密码找回控制器
* @author TMC
* @date 2023/2/20 23:32
* @version 1.0
*/
@Api(value = "密码找回控制器", tags = {"密码找回控制器"})
@RestController
public class FindPasswordController {

    @Autowired
    FindPasswordService findPasswordService;

    @ApiOperation(value = "密码找回接口")
    @PostMapping("/findpassword")
    public RestResponse<String> findPassword(@RequestBody @Validated FindPwdParamsDto findPwdParamsDto) {

        return findPasswordService.findPassword(findPwdParamsDto);

    }

}
