package com.xuecheng.checkcode.service.store;

import com.xuecheng.checkcode.service.CheckCodeService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
* @description 使用redis缓存验证码
* @author TMC
* @date 2023/2/20 21:10
* @version 1.0
*/
@Component("RedisCheckCodeStore")
public class RedisCheckCodeStore implements CheckCodeService.CheckCodeStore{

    @Resource
    RedisTemplate redisTemplate;

    @Override
    public void set(String key, String value, Integer expire) {

        redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);

    }

    @Override
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void remove(String key) {

        redisTemplate.delete(key);

    }
}
