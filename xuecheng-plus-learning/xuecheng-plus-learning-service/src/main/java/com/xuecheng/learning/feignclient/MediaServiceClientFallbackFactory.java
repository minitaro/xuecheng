package com.xuecheng.learning.feignclient;

import com.xuecheng.base.model.RestResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
* @description 远程调用媒资管理服务熔断降级类
* @author TMC
* @date 2023/3/11 17:10
* @version 1.0
*/
@Component
@Slf4j
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable cause) {
        return mediaId -> {
            cause.printStackTrace();
            log.error("调用媒资管理服务获取媒资地址时发生熔断,异常信息:{}", cause.getMessage());
            return RestResponse.validfail("获取媒体文件失败");
        };
    }
}
