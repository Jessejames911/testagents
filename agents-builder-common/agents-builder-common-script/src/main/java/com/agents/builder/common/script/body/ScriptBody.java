package com.agents.builder.common.script.body;


import java.util.Map;

public interface ScriptBody<T> {
    T body(Map<String,Object> params);
}
