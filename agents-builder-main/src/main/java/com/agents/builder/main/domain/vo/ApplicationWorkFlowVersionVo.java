package com.agents.builder.main.domain.vo;

import com.agents.builder.common.core.workflow.LogicFlow;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.ApplicationWorkFlowVersion;
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
 * 视图对象 application_work_flow_version
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ApplicationWorkFlowVersion.class)
public class ApplicationWorkFlowVersionVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long id;

    private String name;
    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("work_flow")
    private LogicFlow workFlow;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("application")
    private Long applicationId;


}
