package com.xuecheng.learning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
* @description 令牌服务配置类
* @author TMC
* @date 2023/2/19 14:01
* @version 1.0
*/
@Configuration
public class TokenServiceConfig {

    //1.JWT签名密钥,与认证服务约定一致
    private String SIGNING_KEY = "mq123";

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

   /*
   @Bean
    public TokenStore tokenStore() {
        //使用内存存储令牌（普通令牌）
        return new InMemoryTokenStore();
    }
    */


}
