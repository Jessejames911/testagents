package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.Problem;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.*;
import jakarta.validation.constraints.*;

/**
 * 业务对象 problem
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Problem.class, reverseConvertGenerate = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemBo extends BaseEntity {

    /**
     *
     */
    private Long id;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String content;

    /**
     *
     */
    @JsonProperty("hit_num")
    private Long hitNum;

    /**
     *
     */
    @JsonProperty("dataset_id")
    private Long datasetId;



    private Long documentId;


    private Long paragraphId;

    private Long mappingId;

}
