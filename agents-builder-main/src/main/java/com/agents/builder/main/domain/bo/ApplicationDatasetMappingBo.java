package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.ApplicationDatasetMapping;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.*;
import jakarta.validation.constraints.*;

/**
 * 业务对象 application_dataset_mapping
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ApplicationDatasetMapping.class, reverseConvertGenerate = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDatasetMappingBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long id;

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
    @JsonProperty("dataset_id")
    private Long datasetId;


}
