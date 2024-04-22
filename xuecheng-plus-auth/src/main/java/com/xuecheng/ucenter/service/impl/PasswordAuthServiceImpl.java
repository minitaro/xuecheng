package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XcException;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @description 账号密码认证服务
* @author TMC
* @date 2023/2/19 20:41
* @version 1.0
*/
@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {

    @Resource
    XcUserMapper xcUserMapper;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    CheckCodeClient checkCodeClient;

    @Override
    public XcUserExt authenticate(AuthParamsDto authParamsDto) {

        //1.校验验证码
        String key = authParamsDto.getCheckcodekey();
        String code = authParamsDto.getCheckcode();
        if (StringUtils.isBlank(key) || StringUtils.isBlank(code)) {
            XcException.cast("验证码不能为空");
        }
        Boolean verify = checkCodeClient.verify(key, code);
        if (verify == null || !verify) {
            XcException.cast("验证码输入错误");
        }

        //2.根据账号查询用户信息
        String username = authParamsDto.getUsername();
        if (StringUtils.isBlank(username)) {
            XcException.cast("账号不能为空");
        }
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (user == null) {
            XcException.cast("账号不存在");
        }

        //3.密码比对
        String passwordDb = user.getPassword();
        String passwordForm = authParamsDto.getPassword();
        boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
        if (!matches) {
            XcException.cast("账号或者密码错误");
        }

        //4.验证通过,封装用户信息返回
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user, xcUserExt);
        return xcUserExt;
    }
}
