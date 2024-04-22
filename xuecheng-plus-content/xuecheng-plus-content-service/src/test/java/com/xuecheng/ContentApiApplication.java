package com.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
* @description 内容管理模块启动类
* @author TMC
* @date 2023/2/5 23:49
* @version 1.0
*/
@EnableFeignClients(basePackages = {"com.xuecheng.content.feignclient"})
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication
public class ContentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentApiApplication.class, args);
    }
}
