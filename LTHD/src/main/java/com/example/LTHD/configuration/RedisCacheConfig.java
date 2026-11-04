package com.example.LTHD.configuration;


import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableCaching
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

        var jsonMapper = JsonMapper.builder().build();

        var jsonSerializer =
                new JacksonJsonRedisSerializer<>(jsonMapper, Object.class);

        RedisCacheConfiguration cacheConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(jsonSerializer)
                        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }
}