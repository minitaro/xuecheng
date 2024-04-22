package com.xuecheng.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
* @description 令牌配置类
* @author TMC
* @date 2023/2/19 15:21
* @version 1.0
*/
@Configuration
public class TokenServiceConfig {

    //1.JWT签名密钥,与认证服务约定一致
    String SIGNING_KEY = "mq123";


    //2.配置JWT令牌转换器
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SIGNING_KEY);
        return converter;
    }


    //3.配置JWT令牌仓库
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }



}
