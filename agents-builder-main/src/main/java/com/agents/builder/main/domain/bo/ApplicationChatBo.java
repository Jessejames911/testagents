package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.ApplicationChat;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 业务对象 application_chat
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ApplicationChat.class, reverseConvertGenerate = false)
public class ApplicationChatBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long id;

    /**
     *
     */
    @JsonProperty("abstract")
    private String abstractName;

    /**
     *
     */
    @JsonProperty("application")
    private Long applicationId;

    /**
     *
     */
    @JsonProperty("client_id")
    private Long clientId;


    @JsonProperty("history_day")
    private String historyDay;

    @JsonProperty("min_star")
    private Integer minStar;

    @JsonProperty("min_trample")
    private Integer minTrample;

    private String comparer;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date start_time;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date end_time;
}
