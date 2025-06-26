package com.agents.builder.main.domain.vo;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.ApplicationChat;
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
 * 视图对象 application_chat
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ApplicationChat.class)
public class ApplicationChatVo extends BaseEntity implements Serializable {

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
    @JsonProperty("abstract")
    private String abstractName;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("application_id")
    private Long applicationId;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("client_id")
    private Long clientId;


    @JsonProperty("chat_record_count")
    private Integer chatRecordCount;

    @JsonProperty("mark_sum")
    private Integer markSum;

    @JsonProperty("star_num")
    private Integer starNum;

    @JsonProperty("trample_num")
    private Integer trampleNum;



}
