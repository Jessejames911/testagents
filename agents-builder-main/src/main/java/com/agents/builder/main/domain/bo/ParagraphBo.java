package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.Paragraph;
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
 * 业务对象 paragraph
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Paragraph.class, reverseConvertGenerate = false)
public class ParagraphBo extends BaseEntity {

    /**
     *
     */
    private Long id;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class })
    private String content;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, })
    private String title;

    /**
     *
     */
    private String status;;

    /**
     *
     */
    @JsonProperty("hit_num")
    private Long hitNum;

    /**
     *
     */
    @JsonProperty("is_active")
    private Boolean isActive;

    /**
     *
     */
    @JsonProperty("dataset_id")
    private Long datasetId;

    /**
     *
     */
    @JsonProperty("document")
    private Long documentId;

    @JsonProperty("problem_list")
    List<ProblemBo> problemList;


}
