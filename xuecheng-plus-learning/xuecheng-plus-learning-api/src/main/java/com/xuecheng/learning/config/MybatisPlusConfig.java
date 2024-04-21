package com.xuecheng.learning.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
* @description MybatisPlus配置类
* @author TMC
* @date 2023/2/6 9:48
* @version 1.0
*/
@Configuration
@MapperScan("com.xuecheng.learning.mapper")
public class MybatisPlusConfig implements MetaObjectHandler {

    // 配置分页拦截器
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return mybatisPlusInterceptor;
    }

    // 配置插入记录的自动填充字段
    @Override
    public void insertFill(MetaObject metaObject) {

        setFieldValByName("createDate", LocalDateTime.now(), metaObject);
        setFieldValByName("updateDate", LocalDateTime.now(), metaObject);

    }

    // 配置更新记录的自动填充字段
    @Override
    public void updateFill(MetaObject metaObject) {

        setFieldValByName("updateDate", LocalDateTime.now(), metaObject);

    }
}
