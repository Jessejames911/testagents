package com.agents.builder.main.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchMode {

    EMBEDDING("embedding","向量检索"),

    KEYWORDS("keywords","全文检索"),

    BLEND("blend","混合检索");


    private String key;

    private String name;


}
