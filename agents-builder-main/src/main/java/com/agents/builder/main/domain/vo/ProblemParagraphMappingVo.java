package com.agents.builder.main.domain.vo;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.ProblemParagraphMapping;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.agents.builder.common.core.excel.annotation.ExcelDictFormat;
import com.agents.builder.common.core.excel.convert.ExcelDictConvert;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 视图对象 problem_paragraph_mapping
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ProblemParagraphMapping.class)
public class ProblemParagraphMappingVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("dataset_id")
    private Long datasetId;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("document")
    private Long documentId;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("paragraph")
    private Long paragraphId;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("problem")
    private Long problemId;


}
