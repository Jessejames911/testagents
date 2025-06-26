package com.agents.builder.common.web.config.properties;

import jodd.net.HttpMethod;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


import java.util.List;

/**
 * Security 配置属性
 *
 *
 */
@Data
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * 排除路径
     */
    private String[] excludes;

    private String[] tempAuthApis;

}
