package com.agents.builder.main.domain.vo;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelFormVo {

    @JsonProperty("input_type")
    private String inputType;

    @JsonProperty("label")
    private Object label;

    @JsonProperty("required")
    private boolean required;

    @JsonProperty("default_value")
    private Object defaultValue;

    @JsonProperty("relation_show_field_dict")
    private java.util.Map<String, Object> relationShowFieldDict;

    @JsonProperty("relation_trigger_field_dict")
    private java.util.Map<String, Object> relationTriggerFieldDict;

    @JsonProperty("trigger_type")
    private String triggerType;

    @JsonProperty("attrs")
    private java.util.Map<String, Object> attrs;

    @JsonProperty("props_info")
    private java.util.Map<String, Object> propsInfo;

    @JsonProperty("field")
    private String field;
}
