package com.agents.builder.main.domain;

import com.agents.builder.common.core.workflow.LogicFlow;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 对象 application_work_flow_version
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "application_work_flow_version",autoResultMap = true)
public class ApplicationWorkFlowVersion extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    private Long id;

    private String name;

    /**
     *
     */
    @TableField(value = "work_flow",
            typeHandler = JacksonTypeHandler.class)
    private LogicFlow workFlow;

    /**
     *
     */
    private Long applicationId;


}
