package com.agents.builder.common.core.enums;

import com.agents.builder.common.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 设备类型
 * 针对多套 用户体系
 *
 *
 */
@Getter
@AllArgsConstructor
public enum UserType {

    /**
     * pc端
     */
    SYS_USER("LOCAL"),

    /**
     * app端
     */
    APP_USER("app_user");

    private final String userType;

    public static UserType getUserType(String str) {
        for (UserType value : values()) {
            if (StringUtils.contains(str, value.getUserType())) {
                return value;
            }
        }
        throw new RuntimeException("'UserType' not found By " + str);
    }
}
