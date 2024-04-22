package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XcException;
import com.xuecheng.base.info.Dictionary;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.mapper.XcUserRoleMapper;
import com.xuecheng.ucenter.model.dto.RegisterParamDto;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.model.po.XcUserRole;
import com.xuecheng.ucenter.service.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.UUID;

/**
* @description 用户注册服务实现类
* @author TMC
* @date 2023/2/21 15:17
* @version 1.0
*/
@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {

    @Resource
    CheckCodeClient checkCodeClient;

    @Resource
    XcUserMapper xcUserMapper;

    @Resource
    XcUserRoleMapper xcUserRoleMapper;

    @Resource
    PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public RestResponse<String> register(RegisterParamDto registerParamDto) {

        //1.校验验证码
        String key = registerParamDto.getCheckcodekey();
        String code = registerParamDto.getCheckcode();
        boolean checkCode = checkCodeClient.verify(key, code);
        if (!checkCode) {
            XcException.cast("验证码错误");
        }

        //2.校验两次密码输入是否一致
        String password = registerParamDto.getPassword();
        String confirmpwd = registerParamDto.getConfirmpwd();
        boolean checkPwd = password.equals(confirmpwd);
        if (!checkPwd) {
            XcException.cast("两次密码输入不一致");
        }

        //3.校验用户名是否存在
        String username = registerParamDto.getUsername();
        LambdaQueryWrapper<XcUser> byUsername = new LambdaQueryWrapper<>();
        byUsername.eq(XcUser::getUsername, username);
        XcUser hitByUsername = xcUserMapper.selectOne(byUsername);
        if (hitByUsername != null) {
            XcException.cast("用户名已被注册");
        }

        //4.校验手机号和邮箱
        //4.1.手机号和邮箱是否已验证
        String cellphone = registerParamDto.getCellphone();
        String email = registerParamDto.getEmail();
        if (!key.equals(cellphone) && !key.equals(email)) {
            XcException.cast("输入的手机号和邮箱没有经过验证");
        }
        //4.2.手机号是否已被注册
        LambdaQueryWrapper<XcUser> byCellphone = new LambdaQueryWrapper<>();
        byCellphone.eq(XcUser::getCellphone, cellphone);
        XcUser hitByCellphone = xcUserMapper.selectOne(byCellphone);
        if (hitByCellphone != null) {
            XcException.cast("手机号已被注册");
        }
        //4.3.邮箱是否已被注册
        LambdaQueryWrapper<XcUser> byEmail = new LambdaQueryWrapper<>();
        byEmail.eq(XcUser::getEmail, email);
        XcUser hitByEmail = xcUserMapper.selectOne(byEmail);
        if (hitByEmail != null) {
            XcException.cast("邮箱已被注册");
        }

        //5.封装XcUser对象并保存
        XcUser xcUser = new XcUser();
        String userId = UUID.randomUUID().toString();
        xcUser.setId(userId);
        xcUser.setUsername(username);
        xcUser.setPassword(passwordEncoder.encode(confirmpwd));
        xcUser.setCellphone(cellphone);
        xcUser.setEmail(email);
        xcUser.setName(username);
        xcUser.setUtype(Dictionary.XcUser.Utype_STUDENT.getCode()); //学生类型
        xcUser.setStatus(Dictionary.XcUser.Status_VALID.getCode()); //用户状态
        int insertXcUser = xcUserMapper.insert(xcUser);
        if (insertXcUser < 1) {
            log.error("保存用户注册信息数据出错");
            XcException.cast("用户注册失败");
        }

        //6.封装XcUserRole对象并保存
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        xcUserRole.setRoleId(Dictionary.XcRole.Id_STUDENT.getCode()); //学生角色
        int insertXcUserRole = xcUserRoleMapper.insert(xcUserRole);
        if (insertXcUserRole < 1) {
            log.error("保存用户角色关系数据出错");
            XcException.cast("用户注册失败");
        }

        return RestResponse.success("用户注册成功,请登录");
    }

}
