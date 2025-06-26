package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.Embedding;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.util.Map;

/**
 * 业务对象 embedding
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Embedding.class, reverseConvertGenerate = false)
public class EmbeddingBo extends BaseEntity {

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String id;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String sourceId;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String sourceType;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("is_active")
    private Boolean isActive;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String embedding;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Map<String,Object> meta;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long datasetId;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long documentId;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long paragraphId;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String searchVector;


}
