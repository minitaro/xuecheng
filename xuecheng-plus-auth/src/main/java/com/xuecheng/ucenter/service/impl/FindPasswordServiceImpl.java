package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XcException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.base.utils.FormUtils;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.FindPwdParamsDto;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.FindPasswordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @description 密码找回服务实现类
* @author TMC
* @date 2023/2/21 13:05
* @version 1.0
*/
@Service
public class FindPasswordServiceImpl implements FindPasswordService {

    @Resource
    CheckCodeClient checkCodeClient;

    @Resource
    XcUserMapper xcUserMapper;

    @Resource
    PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public RestResponse<String> findPassword(FindPwdParamsDto findPwdParamsDto) {

        //1.校验验证码
        String key = findPwdParamsDto.getCheckcodekey();
        String code = findPwdParamsDto.getCheckcode();
        boolean checkCode = checkCodeClient.verify(key, code);
        if (!checkCode) {
            XcException.cast("验证码错误");
        }

        //2.校验两次密码输入是否一致
        String password = findPwdParamsDto.getPassword();
        String confirmpwd = findPwdParamsDto.getConfirmpwd();
        boolean checkPwd = password.equals(confirmpwd);
        if (!checkPwd) {
            XcException.cast("两次密码输入不一致");
        }

        //3.校验手机号和邮箱
        String cellphone = findPwdParamsDto.getCellphone();
        String email = findPwdParamsDto.getEmail();
        if (!key.equals(cellphone) && !key.equals(email)) {
            XcException.cast("输入的手机号和邮箱没有经过验证");
        }

        //4.根据手机号和邮箱查询用户
        boolean checkCellphone = StringUtils.isNotBlank(cellphone) && FormUtils.checkCellphone(cellphone);
        boolean checkEmail = StringUtils.isNotBlank(email) && FormUtils.checkEmail(email);
        LambdaQueryWrapper<XcUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(checkCellphone, XcUser::getCellphone, cellphone);
        queryWrapper.eq(checkEmail, XcUser::getEmail, email);
        XcUser xcUser = xcUserMapper.selectOne(queryWrapper);
        if (xcUser == null) {
            XcException.cast("用户不存在");
        }
        xcUser.setPassword(passwordEncoder.encode(confirmpwd));
        int updateUser = xcUserMapper.updateById(xcUser);
        if (updateUser < 1) {
            XcException.cast("用户修改密码失败");
        }
        return RestResponse.success("密码修改成功,请登录");
    }
}
