package com.xuecheng.checkcode.service.codegenerator;

import com.xuecheng.checkcode.service.CheckCodeService;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
* @description 数字验证码生成器
* @author TMC
* @date 2023/2/20 20:18
* @version 1.0
*/
@Component("NumberCheckCodeGenerator")
public class NumberCheckCodeGenerator implements CheckCodeService.CheckCodeGenerator {
    @Override
    public String generate(int length) {
        String str="0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0; i<length; i++){
            int number=random.nextInt(10);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
