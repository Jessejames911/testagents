package com.agents.builder.main.domain;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Map;

/**
 * 对象 model
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "model",autoResultMap = true)
public class Model extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Long id;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String model_type;

    /**
     *
     */
    private String modelName;

    /**
     *
     */
    private String provider;

    /**
     *
     */
    @TableField(value = "credential", typeHandler = JacksonTypeHandler.class)
    private Credential credential;


    /**
     *
     */
    @TableField(value = "meta",typeHandler = JacksonTypeHandler.class)
    private Map<String,Object> meta;

    /**
     *
     */
    private String status;

    /**
     *
     */
    private String permissionType;


    @Data
    public static class Credential{

        @JsonProperty("api_key")
        private String apiKey;

        @JsonProperty("api_base")
        private String apiBase;
    }
}
