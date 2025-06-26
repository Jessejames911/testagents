package com.agents.builder.main.domain.bo;

import com.agents.builder.common.core.workflow.LogicFlow;
import com.agents.builder.main.domain.ApplicationWorkFlowVersion;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 业务对象 application_work_flow_version
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ApplicationWorkFlowVersion.class, reverseConvertGenerate = false)
public class ApplicationWorkFlowVersionBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long id;

    private String name;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class })
    @JsonProperty("work_flow")
    private LogicFlow workFlow;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class })
    @JsonProperty("application")
    private Long applicationId;


}
