package com.xuecheng.auth.controller;

import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.impl.WxAuthServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
* @description 微信认证登录控制器
* @author TMC
* @date 2023/2/20 11:43
* @version 1.0
*/
@Api(value = "微信认证登录控制器", tags = {"微信认证登录控制器"})
@Controller
@Validated
public class WxLoginController {

    @Resource
    WxAuthServiceImpl wxAuthService;

    @ApiOperation(value = "获取微信用户信息接口")
    @RequestMapping("/wxLogin")
    public String wxLogin(@RequestParam("code") @NotNull(message = "微信授权码不能为空") String code,
                          @RequestParam("state") String state) {

        //1.携带授权码向微信申请令牌,查询用户信息
        XcUser userInfo = wxAuthService.userInfo(code, state);
        if (userInfo == null) {
            //微信查询用户信息失败,重定向到错误页面
            return "redirect:http://www.51xuecheng.cn/error.html";
        }

        //2.向认证服务提交用户信息认证请求
        String username = userInfo.getUsername();
        return "redirect:http://www.51xuecheng.cn/sign.html?authType=wx&username=" + username;

    }

}
