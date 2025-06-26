package com.agents.builder.main.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppType {

    SIMPLE("SIMPLE", "通用应用"),

    WORKFLOW("WORK_FLOW", "工作流应用")
    ;


    private String key;

    private String name;

    public static AppType getAppType(String type) {
        for (AppType appType : AppType.values()) {
            if (appType.name().equalsIgnoreCase(type)) {
                return appType;
            }
        }
        return null;
    }
}
