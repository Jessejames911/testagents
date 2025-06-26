package com.agents.builder.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperateType {

    USE("USE"),

    MANAGE("MANAGE");

    private String key;
}
