package com.agents.builder.main.domain;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 对象 problem_paragraph_mapping
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("problem_paragraph_mapping")
public class ProblemParagraphMapping extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
    private Long datasetId;

    /**
     *
     */
    private Long documentId;

    /**
     *
     */
    private Long paragraphId;

    /**
     *
     */
    private Long problemId;


}
