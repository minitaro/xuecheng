package com.xuecheng.orders.config;

import com.alipay.api.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* @description 支付宝客户端配置类
* @author TMC
* @date 2023/2/27 14:20
* @version 1.0
*/
@Configuration
@Data
public class AlipayClientConfig {

    @Value("${alipay.app-id}")
    private String appId;
    @Value("${alipay.seller-id}")
    private String sellerId;
    @Value("${alipay.gateway-url}")
    private String gatewayUrl;
    @Value("${alipay.merchant-private-key}")
    private String merchantPrivateKey;
    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;
    @Value("${alipay.notify-url}")
    private String notifyUrl;

    //配置支付宝客户端
    @Bean
    public AlipayClient alipayClient() throws AlipayApiException {

        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(gatewayUrl);//设置网关地址
        alipayConfig.setAppId(appId);//设置应用Id
        alipayConfig.setPrivateKey(merchantPrivateKey);//设置应用私钥
        alipayConfig.setAlipayPublicKey(alipayPublicKey);//设置支付宝公钥
        alipayConfig.setFormat(AlipayConstants.FORMAT_JSON);//设置请求格式,固定值json
        alipayConfig.setCharset(AlipayConstants.CHARSET_UTF8);//设置字符集
        alipayConfig.setSignType(AlipayConstants.SIGN_TYPE_RSA2);//设置签名类型

        //构造client
        DefaultAlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);

        return alipayClient;

    }

}
