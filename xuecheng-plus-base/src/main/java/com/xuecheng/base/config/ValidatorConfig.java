package com.xuecheng.base.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
* @description 校验器配置类
* @author TMC
* @date 2023/3/3 19:53
* @version 1.0
*/
@Configuration
public class ValidatorConfig {

    //配置校验器
    @Bean
    public Validator validator(AutowireCapableBeanFactory springFactory) {

        try (ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(true) //快速失败
                .constraintValidatorFactory(new SpringConstraintValidatorFactory(springFactory)) //针对检验实现类含有需注入的Bean的情况
                .buildValidatorFactory()){

            return factory.getValidator();

        }
    }
}
