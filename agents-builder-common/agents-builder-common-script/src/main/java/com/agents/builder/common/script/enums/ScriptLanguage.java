package com.agents.builder.common.script.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ScriptLanguage {

    PYTHON("python","python"),

    JAVASCRIPT("javascript","javascript"),

    JAVA("java","java")

    ;

    private String key;

    private String name;

    public static ScriptLanguage getByKey(String key){
        for(ScriptLanguage scriptLanguage : ScriptLanguage.values()){
            if(scriptLanguage.getKey().equals(key)){
                return scriptLanguage;
            }
        }
        return null;
    }
}
