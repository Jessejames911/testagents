package com.agents.builder.main.domain;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;
import java.util.Map;

/**
 * 对象 function_lib
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "function_lib",autoResultMap = true)
public class FunctionLib extends BaseEntity {

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
    private String code;

    /**
     *
     */
    @TableField(value = "input_field_list",typeHandler = JacksonTypeHandler.class)
    private List<Map<String,Object>> inputFieldList;
    /**
     *
     */
    private Boolean isActive;

    /**
     *
     */
    private String permissionType;


}
