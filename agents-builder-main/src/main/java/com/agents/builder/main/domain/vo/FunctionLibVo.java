package com.agents.builder.main.domain.vo;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.FunctionLib;
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
 * 视图对象 function_lib
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = FunctionLib.class)
public class FunctionLibVo extends BaseEntity implements Serializable {

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
    private String code;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("input_field_list")
    private List<Map<String,Object>> inputFieldList;

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
    @JsonProperty("permission_type")
    private String permissionType;


    private String language;
}
