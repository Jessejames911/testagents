package com.agents.builder.main.domain;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 对象 team_member
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@TableName("team_member")
public class TeamMember {

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
    private Long teamId;

    /**
     *
     */
    private Long userId;


}
