package com.agents.builder.main.domain.vo;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.ApplicationChatRecord;
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
 * 视图对象 application_chat_record
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ApplicationChatRecord.class)
public class ApplicationChatRecordVo extends BaseEntity implements Serializable {

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
    @JsonProperty("vote_status")
    private String voteStatus;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("problem_text")
    private String problemText;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("answer_text")
    private String answerText;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("message_tokens")
    private Long messageTokens;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("answer_tokens")
    private Long answerTokens;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long cost;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("execution_details")
    private List<Map<String, Object>> details;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("improve_paragraph_id_list")
    private List<Long> improveParagraphIdList = List.of();

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("run_time")
    private Double runTime;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("index")
    private Long recordIndex;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("chat_id")
    private Long chatId;

    private Long applicationId;

    @JsonProperty("dataset_list")
    private List<DatasetVo> datasetList;

    @JsonProperty("paragraph_list")
    private List<ParagraphVo> paragraphList;
}
