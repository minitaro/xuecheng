package com.xuecheng.checkcode.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
* @description 邮件服务
* @author TMC
* @date 2023/2/24 12:08
* @version 1.0
*/
@Component
public class QQEmailClient {

    @Value("${spring.mail.username}")
    private String username;

    @Resource
    JavaMailSender javaMailSender;


    /**
    * @description 发送普通邮件
    * @param email 收件地址
     * @param subject 邮件标题
     * @param text 邮件内容
    * @return void
    * @author TMC
    * @date 2023/2/24 13:27
    */
    public void sendSimpleMail(String email, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(email);
        message.setSentDate(new Date());
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);

    }
}
