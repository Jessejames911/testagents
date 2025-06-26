package com.agents.builder.main.domain.vo;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.Dataset;
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
 * 视图对象 dataset
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Dataset.class)
public class DatasetVo extends BaseEntity implements Serializable {

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
    @JsonProperty("desc")
    private String description;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String type;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Map<String,Object> meta;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("embedding_mode_id")
    private Long embeddingModeId;


    @JsonProperty("application_id_list")
    private List<Long> applicationIdList;

    @JsonProperty("application_mapping_count")
    private Integer applicationMappingCount;

    @JsonProperty("char_length")
    private Long charLength;

    @JsonProperty("document_count")
    private Integer documentCount;
}
