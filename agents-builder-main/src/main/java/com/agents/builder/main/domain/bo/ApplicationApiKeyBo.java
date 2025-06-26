package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.ApplicationApiKey;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.util.Set;

/**
 * 业务对象 application_api_key
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ApplicationApiKey.class, reverseConvertGenerate = false)
public class ApplicationApiKeyBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long id;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("secret_key")
    private String secretKey;

    /**
     *
     */
    @JsonProperty("is_active")
    private Boolean isActive;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("application")
    private Long applicationId;


    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("allow_cross_domain")
    private Boolean allowCrossDomain;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("cross_domain_list")
    private Set<String> crossDomainList;


}
