package com.agents.builder.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum MessageEnum {

    AI("assistant", "AI"),

    USER("user", "用户"),

    FUNCTION("function", "函数"),

    SYSTEM("system", "系统");

    private String key;

    private String name;

    public static MessageEnum getByKey(String key) {
        for (MessageEnum messageEnum : MessageEnum.values()) {
            if (messageEnum.getKey().equals(key)) {
                return messageEnum;
            }
        }
        return null;
    }

}
