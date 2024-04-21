package com.xuecheng.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
* @description 跨域配置类
* @author TMC
* @date 2023/2/6 16:01
* @version 1.0
*/
@Configuration
public class CorsConfig {

    /**
    * @description 跨域过滤器
    * @return org.springframework.web.filter.CorsFilter
    * @author TMC
    * @date 2023/2/6 16:09
    */
    @Bean
    public CorsFilter corsFilter() {

        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*"); //允许白名单域名进行跨域调用
        config.setAllowCredentials(true); //允许跨域发送cookie
        config.addAllowedHeader("*"); //放行全部原始头信息
        config.addAllowedMethod("*"); //允许所有请求方法跨域调用
        config.setMaxAge(300L); //缓存时间5分钟,缓存时间段内相同跨域请求不再检查

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); //允许访问所有资源

        return new CorsFilter(source);
    }
}
