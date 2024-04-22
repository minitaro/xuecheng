package com.xuecheng.ucenter.service;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.ucenter.model.dto.RegisterParamDto;

/**
* @description 用户注册服务
* @author TMC
* @date 2023/2/21 15:08
* @version 1.0
*/
public interface RegisterService {

    /**
    * @description 注册用户
    * @param registerParamDto 用户注册请求dto
    * @return com.xuecheng.base.model.RestResponse<java.lang.String>
    * @author TMC
    * @date 2023/2/21 15:17
    */
    RestResponse<String> register(RegisterParamDto registerParamDto);

}
