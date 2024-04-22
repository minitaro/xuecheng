package com.xuecheng.checkcode.service.impl;

import com.xuecheng.base.exception.XcException;
import com.xuecheng.base.utils.FormUtils;
import com.xuecheng.checkcode.client.AliMsgClient;
import com.xuecheng.checkcode.client.QQEmailClient;
import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import com.xuecheng.checkcode.service.SendCheckCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @description 发送验证码服务实现类
* @author TMC
* @date 2023/2/20 22:53
* @version 1.0
*/
@Slf4j
@Service
public class SendCheckCodeServiceImpl implements SendCheckCodeService {

    @Resource
    QQEmailClient emailClient;

    @Resource
    AliMsgClient msgClient;

    @Override
    public void sendCheckCode(CheckCodeParamsDto checkCodeParamsDto, CheckCodeResultDto generate) {

        //1.待发送的验证码
        String code = generate.getAliasing();

        //2.发送方式
        String way = checkCodeParamsDto.getParam1();

        //3.发送验证码
        if (FormUtils.checkCellphone(way)) {
            //3.1.通过短信发送验证码
            try {
                msgClient.sendSms(way, code);
            } catch (Exception e) {
                log.error("短信发送验证码过程出错:{}", e.getMessage());
                XcException.cast("短信发送验证码失败");
            }
        } else if (FormUtils.checkEmail(way)) {
            //3.2.通过邮件发送验证码
            try {
                emailClient.sendSimpleMail(way, "验证码", code);
            }catch (Exception e) {
                log.error("邮件发送验证码过程出错:{}", e.getMessage());
                XcException.cast("邮件发送验证码失败");
            }
        } else {
            //3.3.手机号或者邮箱不合法
            XcException.cast("请输入合法的手机号或者邮箱");

        }

    }
}
