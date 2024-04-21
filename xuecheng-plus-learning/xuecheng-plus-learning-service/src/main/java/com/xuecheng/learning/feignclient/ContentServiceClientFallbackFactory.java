package com.xuecheng.learning.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
* @description 远程调用内容管理服务熔断降级类
* @author TMC
* @date 2023/3/11 3:35
* @version 1.0
*/
@Component
@Slf4j
public class ContentServiceClientFallbackFactory implements FallbackFactory<ContentServiceClient> {
    @Override
    public ContentServiceClient create(Throwable cause) {
        return courseId -> {
            cause.printStackTrace();
            //降级方法
            log.error("调用内容管理服务查询发布课程时发生熔断,异常信息:{}", cause.getMessage());
            return null;
        };
    }
}
