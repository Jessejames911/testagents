package com.agents.builder.main.domain;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Set;

/**
 * 对象 application_api_key
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "application_api_key",autoResultMap = true)
public class ApplicationApiKey extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
    private String secretKey;

    /**
     *
     */
    private Boolean isActive;

    /**
     *
     */
    private Long applicationId;


    /**
     *
     */
    private Boolean allowCrossDomain;

    /**
     *
     */
    @TableField(value = "cross_domain_list", typeHandler = JacksonTypeHandler.class)
    private Set<String> crossDomainList;


}
