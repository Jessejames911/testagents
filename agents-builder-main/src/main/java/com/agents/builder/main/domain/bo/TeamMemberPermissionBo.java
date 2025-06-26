package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.TeamMemberPermission;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Map;

/**
 * 业务对象 team_member_permission
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@AutoMapper(target = TeamMemberPermission.class, reverseConvertGenerate = false)
public class TeamMemberPermissionBo {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("type")
    private String authTargetType;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("target_id")
    private Long target;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Map<String, Boolean> operate;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long memberId;


}
