package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
* @description 远程调用媒资服务熔断降级工厂
* @author TMC
* @date 2023/2/17 19:51
* @version 1.0
*/
@Slf4j
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable throwable) {
        return (filedata, folder, objectName) -> {
            throwable.printStackTrace();
            //降级方法
            log.debug("调用媒资管理服务上传文件时发生熔断,异常信息:{}", throwable.getMessage());
            return null;
        };
    }
}
