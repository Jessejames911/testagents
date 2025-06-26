package com.agents.builder.common.core.config.properties;


import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 系统资源配置
 *
 * @author Angus
 * @date 2024/11/22
 */
@Data
@Component
@ConfigurationProperties(prefix = "system-config-resource")
public class SystemConfigResourceProperties {


    private String jythonPath;

    private String embedJsPath;

    private String defaultWorkflowJsPath;

    private String codeEmailTemplatePath;

    private String qaExcelTemplatePath;

    private String qaCsvTemplatePath;

    private String tableExcelTemplatePath;

    private String tableCsvTemplatePath;
}
