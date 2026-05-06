package com.commonservice.common.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CacheService.class)
    public CacheService cacheService(StringRedisTemplate redisTemplate) {
        return new RedisCacheService(redisTemplate);
    }
}
