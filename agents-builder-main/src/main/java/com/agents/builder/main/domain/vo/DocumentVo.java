package com.agents.builder.main.domain.vo;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.Document;
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
import java.util.List;
import java.util.Map;


/**
 * 视图对象 document
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Document.class)
public class DocumentVo extends BaseEntity implements Serializable {

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
    private String name;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("char_length")
    private Long charLength;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String status;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("is_active")
    private Boolean isActive;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Integer type;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Map<String,Object> meta;

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
    @JsonProperty("hit_handling_method")
    private String hitHandlingMethod;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("directly_return_similarity")
    private Double directlyReturnSimilarity;

    @JsonProperty("paragraph_count")
    private Integer paragraphCount;


    private List<ParagraphVo> content;


}
