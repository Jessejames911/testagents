package com.agents.builder.common.ai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum LlmProvider {

    OLLAMA("model_ollama_provider","ollama"),

    QWEN("model_qwen_provider","通义千问"),

    LOCAL("model_local_provider","本地AI"),

    OPEN_AI("model_openai_provider","openai");


    private String key;

    private String name;

    public static LlmProvider getByProvider(String provider) {
        for (LlmProvider value : LlmProvider.values()) {
            if (value.getKey().equals(provider)) {
                return value;
            }
        }
        return null;
    }

    public static List<Map<String,String>> getAllProvider() {
        return Arrays.stream(LlmProvider.values())
                .map(item -> Map.of("provider", item.getKey(), "name", item.getName()))
                .collect(Collectors.toList());
    }

}
