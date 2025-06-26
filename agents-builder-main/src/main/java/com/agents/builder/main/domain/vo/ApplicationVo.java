package com.agents.builder.main.domain.vo;

import com.agents.builder.common.core.workflow.LogicFlow;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.Application;
import com.agents.builder.main.domain.ApplicationAccessToken;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * 视图对象 application
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Application.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationVo extends BaseEntity implements Serializable {

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
    private String prologue;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("dialogue_number")
    private Integer dialogueNumber;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("dataset_setting")
    private Application.DatasetSetting datasetSetting;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("model_setting")
    private Application.ModelSetting modelSetting;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("problem_optimization")
    private Boolean problemOptimization;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("model")
    private Long modelId;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String icon;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String type;

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
    @JsonProperty("model_params_setting")
    private Application.ModelParamsSetting modelParamsSetting;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("stt_model")
    private Long sttModelId;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("stt_model_enable")
    private Boolean sttModelEnable;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("tts_model")
    private Long ttsModelId;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("tts_model_enable")
    private Boolean ttsModelEnable;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("tts_type")
    private String ttsType;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("problem_optimization_prompt")
    private String problemOptimizationPrompt;

    @JsonProperty("dataset_id_list")
    private List<Long> datasetIdList;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("clean_time")
    private Integer cleanTime;


    @JsonProperty("show_source")
    private Boolean showSource;

    @JsonProperty("show_history")
    private Boolean showHistory;

    @JsonProperty("draggable")
    private Boolean draggable;

    @JsonProperty("show_guide")
    private Boolean showGuide;

    private String avatar;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("user_avatar")
    private String userAvatar;

    @JsonProperty("float_icon")
    private String floatIcon;


    @JsonProperty("float_icon_url")
    private String floatIconUrl;


    @JsonProperty("user_avatar_url")
    private String userAvatarUrl;

    @JsonProperty("disclaimer")
    private Boolean disclaimer;

    @JsonProperty("disclaimer_value")
    private String disclaimerValue;

    @JsonProperty("custom_theme")
    private ApplicationAccessToken.CustomTheme customTheme;

    @JsonProperty("float_location")
    private ApplicationAccessToken.FloatLocation floatLocation;

}
