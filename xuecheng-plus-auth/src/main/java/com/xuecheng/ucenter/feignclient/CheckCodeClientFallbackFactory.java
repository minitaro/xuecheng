package com.xuecheng.ucenter.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
* @description 远程调用验证码服务熔断降级类
* @author TMC
* @date 2023/3/11 2:18
* @version 1.0
*/
@Component
@Slf4j
public class CheckCodeClientFallbackFactory implements FallbackFactory<CheckCodeClient> {
    @Override
    public CheckCodeClient create(Throwable cause) {
        return (key, code) -> {
            cause.printStackTrace();
            //降级方法
            log.debug("调用验证码服务校验验证码时发生熔断,异常信息:{}", cause.getMessage());
            return null;
        };
    }
}
