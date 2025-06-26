package com.agents.builder.main.domain.vo;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.Embedding;
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
import java.util.Map;


/**
 * 视图对象 embedding
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Embedding.class)
public class EmbeddingVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String id;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String sourceId;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String sourceType;

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
    private String embedding;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Map<String,Object> meta;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long datasetId;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long documentId;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long paragraphId;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String searchVector;


}
