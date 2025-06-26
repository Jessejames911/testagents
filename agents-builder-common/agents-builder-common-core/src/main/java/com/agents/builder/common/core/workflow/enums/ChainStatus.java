package com.agents.builder.common.core.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChainStatus {

    READY("ready","就绪"),

    RUNNING("running","执行中"),

    ERROR("error","发生错误"),

    SUCCESS("success","执行成功"),

    FAIL_END("fail_end","错误结束"),
    ;
    private final String key;

    private final String name;
}
