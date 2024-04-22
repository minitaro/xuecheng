package com.xuecheng.checkcode.controller;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import com.xuecheng.checkcode.service.CheckCodeService;
import com.xuecheng.checkcode.service.SendCheckCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* @description 验证码控制器
* @author TMC
* @date 2023/2/23 2:52
* @version 1.0
*/
@Api(value = "验证码控制器", tags = {"验证码控制器"})
@RestController
public class CheckCodeController {

    @Resource(name = "PicCheckCodeService")
    private CheckCodeService picCheckCodeService;


    //1.注入数字验证码服务(验证码服务有不同实现)
    @Resource(name = "NumCheckCodeService")
    private CheckCodeService numCheckCodeService;

    //2.注入发送验证码服务
    @Resource
    private SendCheckCodeService sendCheckCodeService;



    @ApiOperation(value="生成图片验证码接口", notes="生成图片验证码")
    @PostMapping(value = "/pic")
    public CheckCodeResultDto generatePicCheckCode(CheckCodeParamsDto checkCodeParamsDto){
        return picCheckCodeService.generate(checkCodeParamsDto);
    }

    @ApiOperation(value="校验验证码", notes="校验验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "验证key", required = true, dataType = "String", paramType="query"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String", paramType="query")
    })
    @PostMapping(value = "/verify")
    public Boolean verify(String key, String code){
        Boolean isSuccess = picCheckCodeService.verify(key,code);
        return isSuccess;
    }

    @ApiOperation(value="发送验证码接口", notes="发送验证码接口")
    @PostMapping(value = "/phone")
    public CheckCodeResultDto sendCheckCode(CheckCodeParamsDto checkCodeParamsDto){

        //1.生成并缓存验证码
        CheckCodeResultDto generate = numCheckCodeService.generate(checkCodeParamsDto);

        //2.发送验证码
        sendCheckCodeService.sendCheckCode(checkCodeParamsDto, generate);

        //3.返回验证key
        generate.setAliasing(null);
        return generate;

    }
}
