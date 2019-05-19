package com.huawei.imbp.etl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;


import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 5/3/2019
 */

//Todo setup redis cluster

@Configuration
@RefreshScope
public class RedisConfig {

    @Value("#{${db.redis}}")
    public Map<String, Object> redisConfig;

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(){
        return new LettuceConnectionFactory((String)redisConfig.get("redisServer"), Integer.parseInt((String)redisConfig.get("redisPort")));
    }

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplateString(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, RedisSerializationContext.string());
    }
}
