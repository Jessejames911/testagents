package com.agents.builder.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum EntityStatus {


    WAITING(3, "等待"),

    /**
     * 停用
     */
    DISABLE(2, "停用"),
    /**
     * 启用
     */
    ENABLE(1, "启用"),

    INIT(0, "初始化");


    private final Integer code;
    private final String info;

}
