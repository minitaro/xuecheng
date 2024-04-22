package com.xuecheng.checkcode.client;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.google.gson.Gson;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
* @description 阿里云短信服务客户端
* @author TMC
* @date 2023/2/20 21:30
* @version 1.0
*/
@Configuration
@Slf4j
public class AliMsgClient {

    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;
    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;
    @Value("${aliyun.sms.region}")
    private String region;
    @Value("${aliyun.sms.endpoint}")
    private String endpoint;

    @Resource
    StaticCredentialProvider provider;

    @Resource
    AsyncClient client;

    //1. 配置用户认证信息
    @Bean
    public StaticCredentialProvider staticCredentialProvider() {
        return StaticCredentialProvider.create(
                Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());
    }

    //2. 配置短信服务客户端
    @Bean
    public AsyncClient asyncClient() {
        return AsyncClient.builder()
                .region(region)
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride(endpoint)
                                .setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();
    }

    /**
    * @description 发送短信验证码
    * @param cellphone 手机号
     * @param code 验证码
    * @return void
    * @author TMC
    * @date 2023/3/7 4:35
    */
    public void sendSms(String cellphone, String code) {

        //3. 设置请求参数
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .signName("阿里云短信测试")
                .templateCode("SMS_154950909")
                .phoneNumbers(cellphone)
                .templateParam("{\"code\":\"" + code + "\"}")
                .build();
        SendSmsResponse resp = null;
        try {
            //4. 发送请求
            CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
            resp = response.get();
        } catch (Exception e) {
            log.error("阿里云短信服务发送短信出错");
        }
        System.out.println(new Gson().toJson(resp));

    }

}
