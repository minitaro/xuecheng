package com.xuecheng.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
* @description 网关认证过虑器
* @author TMC
* @date 2023/2/19 15:03
* @version 1.0
*/
@Component
@Slf4j
public class GatewayAuthFilter implements GlobalFilter, Ordered {

    //1.白名单
    private static List<String> whitelist = null;

    static {
        //1.类加载时初始化白名单
        try (
                InputStream resourceAsStream = GatewayAuthFilter.class.getResourceAsStream("/security-whitelist.properties");
        ) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            Set<String> strings = properties.stringPropertyNames();
            whitelist = new ArrayList<>(strings);

        } catch (Exception e) {
            log.error("加载白名单配置文件/security-whitelist.properties出错:{}", e.getMessage());
            e.printStackTrace();
        }

    }

    //2.注入令牌仓库
    @Resource
    private TokenStore tokenStore;

    
    //3.执行拦截过滤
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        
        //3.1.获取请求路径
        String requestUrl = exchange.getRequest().getPath().value();
        
        //3.2.校验请求路径,在白名单上则放行
        AntPathMatcher pathMatcher = new AntPathMatcher();
        boolean match = whitelist.stream().anyMatch(url -> pathMatcher.match(url, requestUrl));
        if (match) {
            return chain.filter(exchange);
        }

        //3.3.校验令牌
        //3.3.1.令牌是否存在
        String token = getToken(exchange);
        if (StringUtils.isBlank(token)) {
            return buildReturnMono("没有认证", exchange);
        }
        //3.3.1.令牌是否有效
        OAuth2AccessToken oAuth2AccessToken;
        try {
            oAuth2AccessToken = tokenStore.readAccessToken(token);
            boolean expired = oAuth2AccessToken.isExpired();
            if (expired) {
                return buildReturnMono("认证令牌已过期", exchange);
            }
            return chain.filter(exchange);
        } catch (InvalidTokenException e) {
            log.info("认证令牌无效: {}", token);
            return buildReturnMono("认证令牌无效", exchange);
        }
    }

    /**
    * @description 从服务网络交换器中获取令牌
    * @param exchange 服务网络交换器
    * @return java.lang.String
    * @author TMC
    * @date 2023/2/22 20:25
    */
    private String getToken(ServerWebExchange exchange) {
        String tokenStr = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (StringUtils.isBlank(tokenStr)) {
            return null;
        }
        String token = tokenStr.split(" ")[1];
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return token;
    }

    /**
    * @description 封装返回Mono序列
    * @param error 错误信息
     * @param exchange 服务网络交换器
    * @return reactor.core.publisher.Mono<java.lang.Void>
    * @author TMC
    * @date 2023/2/22 20:49
    */
    private Mono<Void> buildReturnMono(String error, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        String jsonString = JSON.toJSONString(new RestErrorResponse(error));
        byte[] bits = jsonString.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        return response.writeWith(Mono.just(buffer));
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
