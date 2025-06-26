package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.ProblemParagraphMapping;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 业务对象 problem_paragraph_mapping
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ProblemParagraphMapping.class, reverseConvertGenerate = false)
public class ProblemParagraphMappingBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long id;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("dataset_id")
    private Long datasetId;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("document")
    private Long documentId;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("paragraph")
    private Long paragraphId;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("problem")
    private Long problemId;


}
