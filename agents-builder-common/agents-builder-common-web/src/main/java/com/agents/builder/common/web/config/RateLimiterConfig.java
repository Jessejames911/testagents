package com.agents.builder.common.web.config;

import com.agents.builder.common.web.aspect.RateLimiterAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConfiguration;

/**
 *
 * @date 2023/1/18
 */
@AutoConfiguration(after = RedisConfiguration.class)
public class RateLimiterConfig {

    @Bean
    public RateLimiterAspect rateLimiterAspect() {
        return new RateLimiterAspect();
    }

}
