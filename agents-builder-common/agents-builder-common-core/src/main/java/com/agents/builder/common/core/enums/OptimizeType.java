package com.agents.builder.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OptimizeType {

    DOC("doc"),

    PROMPT("prompt");

    private String key;

}
