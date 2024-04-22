package com.xuecheng.orders.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.annotation.Resource;

/**
* @description 资源服务器配置类(oauth协议)
* @author TMC
* @date 2023/2/19 14:04
* @version 1.0
*/
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    //1.资源标识,授权服务中配置的客户端详情服务所授权访问的资源
    public static final String RESOURCE_ID = "xuecheng-plus";

    //2.注入令牌仓库
    @Resource
    TokenStore tokenStore;

    //3.配置资源服务安全
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID)//资源标识
                .tokenStore(tokenStore)//令牌仓库
                .stateless(true);//无状态认证
    }

    //4.配置安全拦截机制
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()//禁用防csrf攻击机制
                .authorizeRequests()
                //.antMatchers("/r/**","/course/**").authenticated()//必须认证通过的请求
                .anyRequest().permitAll();//其它请求全部放行
    }




}
