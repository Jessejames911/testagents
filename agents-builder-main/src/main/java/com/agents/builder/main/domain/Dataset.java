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
 * 对象 dataset
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "dataset",autoResultMap = true)
public class Dataset extends BaseEntity {

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
    private String name;

    /**
     *
     */
    private String description;

    /**
     *
     */
    private String type;

    /**
     *
     */
    @TableField(value = "meta",typeHandler = JacksonTypeHandler.class)
    private Map<String,Object> meta;


    /**
     *
     */
    private Long embeddingModeId;


}
