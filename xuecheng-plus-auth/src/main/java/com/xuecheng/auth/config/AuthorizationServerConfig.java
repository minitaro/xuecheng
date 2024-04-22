package com.xuecheng.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import javax.annotation.Resource;

/**
* @description 授权服务器配置类(oauth协议)
* @author TMC
* @date 2023/2/22 11:39
* @version 1.0
*/
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    //1.注入认证管理器
    @Resource
    private AuthenticationManager authenticationManager;

    //2.注入加密器
    @Lazy
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    //3.注入令牌服务
    @Resource(name = "customAuthorizationServerTokenServices")
    private AuthorizationServerTokenServices authorizationServerTokenServices;


    //1.配置客户端详情服务
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        clients.inMemory()// 使用in-memory存储
                .withClient("XcWebApp")// client_id 客户端注册id
                .secret(passwordEncoder.encode("XcWebApp"))// client_secret 客户端密钥
                .resourceIds("xuecheng-plus")// 授权访问的资源列表
                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit",
                        "refresh_token")// 该client允许的授权模式
                .scopes("all")// 允许的授权范围
                .autoApprove(false)// false表示跳转到授权页面
                .redirectUris("http://www.51xuecheng.cn");// 客户端接收授权码的重定向地址

    }


    //2.配置授权服务端
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {

        endpoints.authenticationManager(authenticationManager)// 认证管理器
                .tokenServices(authorizationServerTokenServices)// 令牌服务
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);// 获取令牌请求方式为POST

    }

    //3.配置授权服务安全
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {

        security.tokenKeyAccess("permitAll()")// 开放/oauth/token_key接口
                .checkTokenAccess("permitAll()")// 开放/oauth/check_token接口
                .allowFormAuthenticationForClients();// 允许客户端表单认证

    }

}
