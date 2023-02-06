package com.xuecheng;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
* @description 内容管理模块启动类
* @author TMC
* @date 2023/2/5 23:49
* @version 1.0
*/
@EnableSwagger2Doc
@SpringBootApplication
public class ContentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentApiApplication.class, args);
    }
}
