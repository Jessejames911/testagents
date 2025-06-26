package com.agents.builder.common.core.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OperateTargetType {

    APP("APPLICATION"),

    DATASET("DATASET"),

    MODEL("MODEL"),

    FUNCTION_LIB("FUNCTION_LIB")

    ;

    private String key;

    public static OperateTargetType findKey(String key) {
        for (OperateTargetType item : OperateTargetType.values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }
}
