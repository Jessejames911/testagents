package com.agents.builder.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailType {

    REGISTER("register","用户注册"),

    RESET_PASSWORD("reset_password","修改密码");

    private final String type;

    private final String desc;

    public static EmailType getByType(String type) {
        for (EmailType emailType : EmailType.values()) {
            if (emailType.getType().equals(type)) {
                return emailType;
            }
        }
        return null;
    }
}
