package com.agents.builder.main.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FunctionDebugDto {

    private String language;

    private String code;

    @JsonProperty("debug_field_list")
    private List<Map<String, Object>> debugFieldList;

    @JsonProperty("input_field_list")
    private List<Map<String, Object>> inputFieldList;
}
