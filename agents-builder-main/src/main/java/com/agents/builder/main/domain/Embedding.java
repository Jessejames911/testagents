package com.agents.builder.main.domain;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Map;

/**
 * 对象 embedding
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "embedding",autoResultMap = true)
public class Embedding extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private String id;

    /**
     *
     */
    private String sourceId;

    /**
     *
     */
    private String sourceType;

    /**
     *
     */
    private Boolean isActive;

    /**
     *
     */
    private String embedding;

    /**
     *
     */
    @TableField(value = "meta",typeHandler = JacksonTypeHandler.class)
    private Map<String,Object> meta;

    /**
     *
     */
    private Long datasetId;

    /**
     *
     */
    private Long documentId;

    /**
     *
     */
    private Long paragraphId;

    /**
     *
     */
    private String searchVector;


}
