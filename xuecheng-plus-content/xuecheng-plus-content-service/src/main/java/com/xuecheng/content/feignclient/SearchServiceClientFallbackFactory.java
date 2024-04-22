package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
* @description 远程调用搜索服务熔断降级工厂
* @author TMC
* @date 2023/3/11 1:58
* @version 1.0
*/
@Component
@Slf4j
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable cause) {
        return courseIndex -> {
            cause.printStackTrace();
            //降级方法
            log.debug("调用搜索服务添加课程索引时发生熔断,异常信息:{}", cause.getMessage());
            return false;
        };
    }
}
