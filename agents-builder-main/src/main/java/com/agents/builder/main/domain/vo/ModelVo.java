package com.agents.builder.main.domain.vo;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.Model;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;


/**
 * 视图对象 model
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Model.class)
public class ModelVo extends BaseEntity implements Serializable {

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
    private String model_type;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("model_name")
    private String modelName;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String provider;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Model.Credential credential;


    /**
     *
     */
    @ExcelProperty(value = "")
    private Map<String,Object> meta;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String status;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("permission_type")
    private String permissionType;


}
