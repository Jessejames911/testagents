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
 * 对象 team_member_permission
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@TableName(value = "team_member_permission",autoResultMap = true)
public class TeamMemberPermission {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id")
    private Long id;

    /**
     *
     */
    private String authTargetType;

    /**
     *
     */
    private Long target;

    /**
     *
     */
    @TableField(value = "operate", typeHandler = JacksonTypeHandler.class)
    private Map<String, Boolean> operate;

    /**
     *
     */
    private Long memberId;




}
