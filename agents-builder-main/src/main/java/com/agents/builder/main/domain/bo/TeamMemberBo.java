package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.TeamMember;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.util.List;

/**
 * 业务对象 team_member
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@AutoMapper(target = TeamMember.class, reverseConvertGenerate = false)
public class TeamMemberBo {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long id;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long teamId;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;


    @JsonProperty("team_member_permission_list")
    private List<TeamMemberPermissionBo> teamMemberPermissionList;
}
