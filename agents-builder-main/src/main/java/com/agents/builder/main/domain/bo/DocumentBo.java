package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.Document;
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
 * 业务对象 document
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Document.class, reverseConvertGenerate = false)
public class DocumentBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long id;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     *
     */
    @JsonProperty("char_length")
    private Long charLength;

    /**
     *
     */
    private String status;

    /**
     *
     */
    @JsonProperty("is_active")
    private Boolean isActive;

    /**
     *
     */
    private Integer type;

    /**
     *
     */
    private Map<String,Object> meta;

    /**
     *
     */
    @JsonProperty("dataset_id")
    private Long datasetId;

    /**
     *
     */
    @JsonProperty("hit_handling_method")
    private String hitHandlingMethod;

    /**
     *
     */
    @JsonProperty("directly_return_similarity")
    private Double directlyReturnSimilarity;

    private List<ParagraphBo> paragraphs;

    @JsonProperty("id_list")
    private List<Long> idList;

    private Object dep;

}
