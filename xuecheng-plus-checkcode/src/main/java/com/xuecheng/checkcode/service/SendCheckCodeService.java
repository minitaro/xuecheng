package com.xuecheng.checkcode.service;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;

/**
* @description 发送验证码服务
* @author TMC
* @date 2023/2/20 22:47
* @version 1.0
*/
public interface SendCheckCodeService {

    /**
    * @description 发送验证码
    * @param checkCodeParamsDto 验证码请求dto
     * @param generate 验证码响应dto
    * @return void
    * @author TMC
    * @date 2023/2/21 10:52
    */
    void sendCheckCode(CheckCodeParamsDto checkCodeParamsDto, CheckCodeResultDto generate);
}
