package com.xuecheng.ucenter.service;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.model.dto.FindPwdParamsDto;

/**
* @description 密码找回服务
* @author TMC
* @date 2023/2/21 13:03
* @version 1.0
*/
public interface FindPasswordService {

    /**
    * @description 找回密码
    * @param findPwdParamsDto 找回密码请求dto
    * @return com.xuecheng.base.model.RestResponse<java.lang.String>
    * @author TMC
    * @date 2023/2/21 14:16
    */
    RestResponse<String> findPassword(FindPwdParamsDto findPwdParamsDto);

}
