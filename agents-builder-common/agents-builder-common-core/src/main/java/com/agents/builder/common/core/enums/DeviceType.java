package com.agents.builder.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备类型
 * 针对一套 用户体系
 *
 *
 */
@Getter
@AllArgsConstructor
public enum DeviceType {

    /**
     * pc端
     */
    PC("pc"),

    /**
     * social第三方端
     */
    SOCIAL("social");

    private final String device;
}
