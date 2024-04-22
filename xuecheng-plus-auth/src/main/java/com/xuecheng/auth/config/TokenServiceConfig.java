package com.xuecheng.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;

/**
* @description 令牌服务配置类
* @author TMC
* @date 2023/2/22 0:15
* @version 1.0
*/
@Configuration
public class TokenServiceConfig {

    //1.签名密钥
    private String SIGNING_KEY = "mq123";

    //2.注入JWT令牌转换器
    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;

    //3.注入令牌仓库
    @Autowired
    TokenStore tokenStore;


    //4.配置JWT令牌转换器
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SIGNING_KEY);
        return converter;
    }

    //5.配置JWT令牌仓库
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    //6.配置JWT令牌服务
    @Bean(name = "customAuthorizationServerTokenServices" )
    public AuthorizationServerTokenServices tokenService() {
        DefaultTokenServices service=new DefaultTokenServices();//创建令牌服务
        service.setAccessTokenValiditySeconds(7200); //令牌默认有效期2小时
        service.setSupportRefreshToken(true);//支持刷新令牌
        service.setRefreshTokenValiditySeconds(259200);//刷新令牌默认有效期3天
        //JWT令牌转换器
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));
        service.setTokenEnhancer(tokenEnhancerChain);
        service.setTokenStore(tokenStore);//令牌仓库
        return service;
    }

    /*
    @Bean
    public TokenStore tokenStore() {
        //使用内存存储令牌（普通令牌）
        return new InMemoryTokenStore();
    }
    */

}
