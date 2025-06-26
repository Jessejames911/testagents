package com.agents.builder.common.ai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ModelType {

    LLM("大语言模型", "LLM"),

    EMBEDDING("嵌入模型", "EMBEDDING");

    private String name;

    private String key;

    public static ModelType getByKey(String key) {
        for (ModelType modelType : ModelType.values()) {
            if (modelType.getKey().equals(key)) {
                return modelType;
            }
        }
        return null;
    }

    public static List<Map<String,String>> getAllType() {
        return Arrays.stream(ModelType.values())
                .map(item -> Map.of("key", item.getName(), "value", item.getKey()))
                .collect(Collectors.toList());
    }

}
