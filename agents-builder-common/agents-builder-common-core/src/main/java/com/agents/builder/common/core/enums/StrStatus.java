package com.agents.builder.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StrStatus {

    SUCCESS("SUCCESS", "成功"),

    FAIL("FAIL","失败");

    private String key;

    private String desc;
}
