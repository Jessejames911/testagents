package com.agents.builder.main.domain;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 对象 application_chat
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("application_chat")
public class ApplicationChat extends BaseEntity {

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
    @TableField("abstract")
    private String abstractName;

    /**
     *
     */
    private Long applicationId;

    /**
     *
     */
    private Long clientId;


}
