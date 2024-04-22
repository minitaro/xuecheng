package com.xuecheng.media.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @description RedisCache管理器配置类
* @author TMC
* @date 2023/3/10 17:00
* @version 1.0
*/
@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Resource(name = "stringSerializer")
    RedisSerializer<String> stringSerializer;
    @Resource(name = "json_Serializer")
    RedisSerializer<Object> jsonSerializer;

    @Value("${spring.cache.redis.time-to-live}")
    private int ttl;
    @Value("${spring.cache.cache-names}")
    private String[] cacheNames;


    @Bean
    public CacheManager cacheManager(RedisConnectionFactory lettuceConnectionFactory) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();

        defaultCacheConfig = defaultCacheConfig.entryTtl(Duration.ofSeconds(ttl))// 设置缓存的默认过期时间
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer))// 设置 key为string序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));// 设置value为json序列化
                //.disableCachingNullValues();// 不缓存空值

        Set<String> cacheNames = Arrays.stream(this.cacheNames).collect(Collectors.toSet());

        // 对每个缓存空间应用不同的配置
        //Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        //configMap.put(userCacheName, defaultCacheConfig.entryTtl(Duration.ofSeconds(userCacheExpireTime)));

        RedisCacheManager cacheManager = RedisCacheManager.builder(lettuceConnectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .initialCacheNames(cacheNames)
                //.withInitialCacheConfigurations(configMap)
                .build();
        return cacheManager;
    }

}
