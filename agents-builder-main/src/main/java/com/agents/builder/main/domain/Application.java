package com.agents.builder.main.domain;

import com.agents.builder.common.core.workflow.LogicFlow;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.DefaultChatOptions;
import org.springframework.ai.chat.prompt.DefaultChatOptionsBuilder;
import org.springframework.ai.openai.OpenAiChatOptions;

import java.io.Serial;
import java.util.List;

/**
 * 对象 application
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "application",autoResultMap = true)
public class Application extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String description;

    /**
     *
     */
    private String prologue;

    /**
     *
     */
    private Integer dialogueNumber;

    /**
     *
     */
    @TableField(value = "dataset_setting",
            typeHandler = JacksonTypeHandler.class)
    private DatasetSetting datasetSetting;

    /**
     *
     */
    @TableField(value = "model_setting",
            typeHandler = JacksonTypeHandler.class)
    private ModelSetting modelSetting;

    /**
     *
     */
    private Boolean problemOptimization;

    /**
     *
     */
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
    @TableField(value = "work_flow",
            typeHandler = JacksonTypeHandler.class)
    private LogicFlow workFlow;

    /**
     *
     */
    @TableField(value = "model_params_setting",
            typeHandler = JacksonTypeHandler.class)
    private ModelParamsSetting modelParamsSetting;

    /**
     *
     */
    private Long sttModelId;

    /**
     *
     */
    private Boolean sttModelEnable;

    /**
     *
     */
    private Long ttsModelId;

    /**
     *
     */
    private Boolean ttsModelEnable;

    /**
     *
     */
    private String ttsType;

    /**
     *
     */
    private String problemOptimizationPrompt;


    private Integer cleanTime;


    @Data
    public static class DatasetSetting {
        @JsonProperty("top_n")
        private Integer topN;

        @JsonProperty("similarity")
        private Double similarity;

        @JsonProperty("max_paragraph_char_number")
        private Integer maxParagraphCharNumber;

        @JsonProperty("search_mode")
        private String searchMode;

        @JsonProperty("no_references_setting")
        private NoReferencesSetting noReferencesSetting;
    }

    @Data
    public static class NoReferencesSetting {
        @JsonProperty("status")
        private String status;

        @JsonProperty("value")
        private String value;

    }

    @Data
    public static class ModelSetting {

        private String prompt;

        private String system;

        @JsonProperty("no_references_prompt")
        private String noReferencesPrompt;
    }

    @Data
    public static class ModelParamsSetting {

        @JsonProperty("max_tokens")
        private Integer maxTokens;

        private Double temperature;

        @JsonProperty("top_k")
        private Integer topK;

        @JsonProperty("top_p")
        private Double topP;

        @JsonIgnore
        public ChatOptions getChatOptions() {
            return DashScopeChatOptions.builder()
                    .withMaxToken(maxTokens)
                    .withTemperature(temperature)
                    .withTopP(topP)
                    .withTopK(topK)
                    .build();

        }
    }

}
