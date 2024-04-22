package com.xuecheng.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
* @description RestTemplate配置类
* @author TMC
* @date 2023/3/7 15:36
* @version 1.0
*/
@Configuration
public class RestTemplateConfig {

    /**
    * @description 配置okhttp3客户端
    * @return org.springframework.http.client.OkHttp3ClientHttpRequestFactory
    * @author TMC
    * @date 2023/3/7 15:41
    */
    @Bean
    public OkHttp3ClientHttpRequestFactory httpRequestFactory() {
        OkHttp3ClientHttpRequestFactory requestFactory = new OkHttp3ClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(8000);
        requestFactory.setWriteTimeout(8000);
        return requestFactory;
    }



    /**
    * @description RestTemplate集成okhttp3客户端
    * @return org.springframework.web.client.RestTemplate
    * @author TMC
    * @date 2023/3/7 15:42
    */
    @Bean
    public RestTemplate restTemplate() {

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());

        //中文乱码, StringHttpMessageConverter的默认编码为ISO-8859-1
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter converter : list) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
            }
        }

        return restTemplate;
    }

}
