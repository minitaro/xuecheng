package com.xuecheng.checkcode.service.keygenerator;

import com.xuecheng.checkcode.service.CheckCodeService;
import org.springframework.stereotype.Component;

/**
* @description 普通key生成器
* @author TMC
* @date 2023/2/20 22:23
* @version 1.0
*/
@Component("NormalKeyGenerator")
public class NormalKeyGenerator implements CheckCodeService.KeyGenerator{
    @Override
    public String generate(String prefix) {
        return prefix;
    }
}
