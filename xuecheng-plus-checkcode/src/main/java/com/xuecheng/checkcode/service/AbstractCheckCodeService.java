package com.xuecheng.checkcode.service;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
* @description 验证码服务抽象类
* @author TMC
* @date 2023/2/24 15:09
* @version 1.0
*/
@Slf4j
public abstract class AbstractCheckCodeService implements CheckCodeService{

    protected KeyGenerator keyGenerator;
    protected CheckCodeGenerator checkCodeGenerator;
    protected CheckCodeStore checkCodeStore;

    /**
    * @description 验证码生成结果封装类
    * @author TMC
    * @date 2023/2/24 15:11
    */
    @Data
    protected class GenerateResult{
        /**
         * 验证码key
         */
        String key;

        /**
         * 验证码
         */
        String code;
    }

    //1.验证码服务组件接口初始化方法
    /**
     * @description 验证码key生成器初始化
     * @param keyGenerator 验证码key生成器
     * @return void
     * @author TMC
     * @date 2023/2/24 15:13
     */
    public abstract void  setKeyGenerator(KeyGenerator keyGenerator);

    /**
    * @description 验证码生成器初始化
    * @param checkCodeGenerator 验证码生成器
    * @return void
    * @author TMC
    * @date 2023/2/24 15:12
    */
    public abstract void  setCheckCodeGenerator(CheckCodeGenerator checkCodeGenerator);

    /**
    * @description 验证码仓库初始化
    * @param CheckCodeStore 验证码仓库
    * @return void
    * @author TMC
    * @date 2023/2/24 15:14
    */
    public abstract void  setCheckCodeStore(CheckCodeStore CheckCodeStore);

    //2.验证码生成与缓存
    /**
    * @description 验证码生成与缓存
    * @param checkCodeParamsDto 验证码请求dto
    * @return com.xuecheng.checkcode.model.CheckCodeResultDto
    * @author TMC
    * @date 2023/2/24 15:15
    */
    public abstract CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto);

    //1.验证码生成和缓存的一般逻辑
    /**
    * @description 验证码生成和缓存的一般逻辑
     * @param code_length 验证码长度
     * @param key_prefix 验证码key前缀
     * @param expire 验证码缓存有效时间
    * @return com.xuecheng.checkcode.service.AbstractCheckCodeService.GenerateResult
    * @author TMC
    * @date 2023/2/24 15:21
    */
    public GenerateResult generate(Integer code_length, String key_prefix, Integer expire){

        //1.1.生成验证码key
        String key = keyGenerator.generate(key_prefix);
        //1.2.生成验证码
        String code = checkCodeGenerator.generate(code_length);
        log.debug("生成验证码:{}",code);
        //1.3.存储验证码
        checkCodeStore.set(key, code, expire);
        //1.4.封装验证码生成结果并返回
        GenerateResult generateResult = new GenerateResult();
        generateResult.setKey(key);
        generateResult.setCode(code);
        return generateResult;
    }



    //2.验证码校验的一般逻辑实现
    public boolean verify(String key, String code){
        if (StringUtils.isBlank(key) || StringUtils.isBlank(code)){
            return false;
        }
        String code_l = checkCodeStore.get(key);
        if (code_l == null){
            return false;
        }
        boolean result = code_l.equalsIgnoreCase(code);
        if(result){
            //删除验证码缓存
            checkCodeStore.remove(key);
        }
        return result;
    }
}
