package com.xuecheng.checkcode.service.impl;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import com.xuecheng.checkcode.service.AbstractCheckCodeService;
import com.xuecheng.checkcode.service.CheckCodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description 数字验证码服务实现类
 * @author TMC
 * @date 2023/2/20 22:15
 * @version 1.0
 */
@Service("NumCheckCodeService")
public class NumCheckCodeServiceImpl extends AbstractCheckCodeService implements CheckCodeService {

    //验证码服务组件接口初始化
    @Resource(name = "NormalKeyGenerator")
    @Override
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    @Resource(name = "NumberCheckCodeGenerator")
    @Override
    public void setCheckCodeGenerator(CheckCodeGenerator checkCodeGenerator) {
        this.checkCodeGenerator = checkCodeGenerator;
    }

    @Resource(name = "RedisCheckCodeStore")
    @Override
    public void setCheckCodeStore(CheckCodeStore CheckCodeStore) {
        this.checkCodeStore = CheckCodeStore;
    }

    //验证码生成与缓存一般逻辑已在抽象类中实现,这里主要实现验证码请求和响应的数据处理
    @Override
    public CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto) {

        //1.邮件或短信验证码的key直接使用邮箱或者手机号
        String key = checkCodeParamsDto.getParam1();
        //2.验证码长度为4位,有效时间5分钟
        GenerateResult generate = this.generate(4, key, 300);
        //3.封装验证码生成结果
        CheckCodeResultDto checkCodeResultDto = new CheckCodeResultDto();
        String code = generate.getCode();
        checkCodeResultDto.setAliasing(code);
        checkCodeResultDto.setKey(key);
        return checkCodeResultDto;
    }
}
