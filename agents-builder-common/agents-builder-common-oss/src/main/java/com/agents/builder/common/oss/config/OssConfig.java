package com.agents.builder.common.oss.config;


import com.agents.builder.common.oss.config.properties.OssProperties;
import com.agents.builder.common.oss.core.OssClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(OssProperties.class)
public class OssConfig {

    @Bean
    @ConditionalOnProperty(value = "oss.enabled", havingValue = "true")
    public OssClient ossClient(OssProperties ossProperties) {
        return new OssClient(ossProperties);
    }
}
