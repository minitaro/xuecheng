package com.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
* @description 学习中心服务启动类
* @author TMC
* @date 2023/2/28 17:05
* @version 1.0
*/
@EnableFeignClients(basePackages={"com.xuecheng.learning.feignclient"})
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication
public class LearningApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningApiApplication.class, args);
    }


}
