package com.xuecheng.checkcode.service;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;

/**
* @description 验证码服务
* @author TMC
* @date 2023/2/24 14:35
* @version 1.0
*/
public interface CheckCodeService {

    /**
    * @description 生成验证码
    * @param checkCodeParamsDto 验证码请求dto
    * @return com.xuecheng.checkcode.model.CheckCodeResultDto
    * @author TMC
    * @date 2023/2/24 14:36
    */
    CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto);

    /**
    * @description 校验验证码
    * @param key 验证key
     * @param code 验证码
    * @return boolean
    * @author TMC
    * @date 2023/2/24 14:37
    */
    public boolean verify(String key, String code);

    /**
    * @description 验证key生成器
    * @author TMC
    * @date 2023/2/24 14:38
    */
    public interface KeyGenerator{

        /**
        * @description 生成验证key
        * @param prefix 验证key的前缀
        * @return java.lang.String
        * @author TMC
        * @date 2023/2/24 14:42
        */
        String generate(String prefix);
    }

    /**
    * @description 验证码生成器
    * @author TMC
    * @date 2023/2/24 14:40
    */
    public interface CheckCodeGenerator{
        /**
        * @description 生成验证码
        * @param length 验证码长度
        * @return java.lang.String
        * @author TMC
        * @date 2023/2/24 14:43
        */
        String generate(int length);
    }

    /**
    * @description 验证码仓库
    * @author TMC
    * @date 2023/2/24 14:41
    */
    public interface CheckCodeStore{

       /**
       * @description 验证码缓存设置
       * @param key 验证key
        * @param value 验证码
        * @param expire 有效时间,单位秒
       * @return void
       * @author TMC
       * @date 2023/2/24 14:42
       */
        void set(String key, String value, Integer expire);

        /**
        * @description 获取缓存验证码
        * @param key 验证key
        * @return java.lang.String
        * @author TMC
        * @date 2023/2/24 14:45
        */
        String get(String key);

        /**
        * @description 删除缓存验证码
        * @param key 验证key
        * @return void
        * @author TMC
        * @date 2023/2/24 14:45
        */
        void remove(String key);
    }



}
