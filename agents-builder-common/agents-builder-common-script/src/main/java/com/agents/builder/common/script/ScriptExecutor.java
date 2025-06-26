package com.agents.builder.common.script;



import com.agents.builder.common.script.enums.ScriptLanguage;

import java.util.Map;

public interface ScriptExecutor {

    ScriptLanguage type();

    Object execute(String script, Map<String,Object> params) throws Exception;

    Object compile(String script) throws Exception;
}
