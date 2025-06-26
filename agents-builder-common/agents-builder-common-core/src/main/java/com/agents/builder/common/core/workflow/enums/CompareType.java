package com.agents.builder.common.core.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompareType {


    EQUAL("eq", "等于"),
    CONTAINS("contain", "包含"),
    GREATER_THAN_OR_EQUAL("ge", "大于等于"),
    GREATER_THAN("ge", "大于"),
    NOT_NULL("is_not_null","不为空"),
    NULL("is_null","为空"),
    LESS_THAN("lt", "小于"),
    LESS_THAN_OR_EQUAL("le", "小于等于"),
    LEN_EQUAL("len_eq", "长度等于"),
    LEN_GREATER_THAN("len_gt", "长度大于"),
    LEN_GREATER_THAN_OR_EQUAL("len_ge", "长度大于等于"),
    LEN_LESS_THAN("len_lt", "长度小于"),
    LEN_LESS_THAN_OR_EQUAL("len_le", "长度小于等于"),
    NOT_EQUAL("notEqual", "不等于"),
    NOT_CONTAINS("not_contain", "不包含");


    private String key;

    private String name;

    public static CompareType getByKey(String key)
    {
        for (CompareType compareType : CompareType.values()) {
            if (compareType.getKey().equals(key)) {
                return compareType;
            }
        }
        return null;
    }
}
