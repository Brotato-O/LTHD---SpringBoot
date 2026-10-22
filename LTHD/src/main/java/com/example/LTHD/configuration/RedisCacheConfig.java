package com.example.LTHD.configuration;


import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class RedisCacheConfig {

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory) {

        // Kiểm tra kết nối Redis khi khởi tạo CacheManager
        try (var connection = connectionFactory.getConnection()) {
            String result = connection.ping();
            System.out.println("Redis PING: " + result);
        } catch (Exception e) {
            System.err.println("Redis connection failed!");
            e.printStackTrace();
        }

        RedisCacheConfiguration cacheConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }
}