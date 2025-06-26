package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.ApplicationPublicAccessClient;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 业务对象 application_public_access_client
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ApplicationPublicAccessClient.class, reverseConvertGenerate = false)
public class ApplicationPublicAccessClientBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long id;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("access_num")
    private Long accessNum;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("intraday_access_num")
    private Long intradayAccessNum;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("application")
    private Long applicationId;


}
