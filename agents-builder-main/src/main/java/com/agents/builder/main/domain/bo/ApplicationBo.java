package com.agents.builder.main.domain.bo;

import com.agents.builder.common.core.workflow.LogicFlow;
import com.agents.builder.main.domain.Application;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 业务对象 application
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Application.class, reverseConvertGenerate = false)
public class ApplicationBo extends BaseEntity {

    /**
     *
     */
    private Long id;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class })
    private String name;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class })
    @JsonProperty("desc")
    private String description;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class })
    private String prologue;

    /**
     *
     */
    @JsonProperty("dialogue_number")
    private Integer dialogueNumber;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class })
    @JsonProperty("dataset_setting")
    private Application.DatasetSetting datasetSetting;

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class })
    @JsonProperty("model_setting")
    private Application.ModelSetting modelSetting;

    /**
     *
     */
    @JsonProperty("problem_optimization")
    private Boolean problemOptimization;

    /**
     *
     */
    @JsonProperty("model_id")
    private Long modelId;

    /**
     *
     */
    private String icon;

    /**
     *
     */
    private String type;

    /**
     *
     */
    @JsonProperty("work_flow")
    private LogicFlow workFlow;

    /**
     *
     */
    @JsonProperty("model_params_setting")
    private Application.ModelParamsSetting modelParamsSetting;

    /**
     *
     */
    @JsonProperty("stt_model_id")
    private Long sttModelId;

    /**
     *
     */
    @JsonProperty("stt_model_enable")
    private Boolean sttModelEnable;

    /**
     *
     */
    @JsonProperty("tts_model_id")
    private Long ttsModelId;

    /**
     *
     */
    @JsonProperty("tts_model_enable")
    private Boolean ttsModelEnable;

    /**
     *
     */
    @JsonProperty("tts_type")
    private String ttsType;

    /**
     *
     */
    @JsonProperty("problem_optimization_prompt")
    private String problemOptimizationPrompt;

    @JsonProperty("dataset_id_list")
    private List<Long> datasetIdList;

    @JsonProperty("clean_time")
    private Integer cleanTime;

    private MultipartFile file;

}
