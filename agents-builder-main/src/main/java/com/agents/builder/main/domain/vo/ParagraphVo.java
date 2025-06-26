package com.agents.builder.main.domain.vo;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.Paragraph;
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
 * 视图对象 paragraph
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Paragraph.class)
public class ParagraphVo extends BaseEntity implements Serializable {

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
    private String content;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String title;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String status;;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("hit_num")
    private Long hitNum;

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
    @JsonProperty("dataset_id")
    private Long datasetId;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("document_id")
    private Long documentId;

    private Double similarity;


    @JsonProperty("char_length")
    private Integer charLength;

    @JsonProperty("dataset_name")
    private String datasetName;

    @JsonProperty("document_name")
    private String documentName;
}
