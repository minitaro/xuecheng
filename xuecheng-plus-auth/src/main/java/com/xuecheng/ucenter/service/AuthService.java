package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;

/**
* @description 认证服务
* @author TMC
* @date 2023/2/19 18:37
* @version 1.0
*/
public interface AuthService {

    /**
    * @description 认证
    * @param authParamsDto 认证请求dto
    * @return com.xuecheng.ucenter.model.dto.XcUserExt
    * @author TMC
    * @date 2023/2/23 0:38
    */
    XcUserExt authenticate(AuthParamsDto authParamsDto);

}