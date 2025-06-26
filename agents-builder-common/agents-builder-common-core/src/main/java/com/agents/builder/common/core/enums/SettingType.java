package com.agents.builder.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SettingType {

    EMAIL(0,"邮箱"),

    RSA(1,"私钥秘钥"),

    DISPLAY(2, "显示");

    private Integer code;

    private String name;
}
