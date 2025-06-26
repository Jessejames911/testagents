package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.Dataset;
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
 * 业务对象 dataset
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Dataset.class, reverseConvertGenerate = false)
public class DatasetBo extends BaseEntity {

    /**
     *
     */
    private Long id;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("desc")
    private String description;

    /**
     *
     */
    private String type;

    /**
     *
     */
    private Map<String,Object> meta;



    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("embedding_mode_id")
    private Long embeddingModeId;


}
