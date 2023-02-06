package com.xuecheng.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
* @description 跨域过虑器配置类
* @author TMC
* @date 2023/2/6 16:01
* @version 1.0
*/
@Configuration
public class GlobalCorsConfig {

    /**
    * @description 允许跨域调用过滤器
    * @return org.springframework.web.filter.CorsFilter
    * @author TMC
    * @date 2023/2/6 16:09
    */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        //1.允许白名单域名进行跨域调用
        config.addAllowedOrigin("*");
        //2.允许跨域发送cookie
        config.setAllowCredentials(true);
        //3.放行全部原始头信息
        config.addAllowedHeader("*");
        //4.允许所有请求方法跨域调用
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
