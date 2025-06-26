package com.agents.builder.common.script.groovy.executor;

import com.agents.builder.common.script.common.FunctionExecutor;

import java.util.Map;


public class HttpExecutor extends FunctionExecutor {
    @Override
    public Object execute(Map<String, Object> params) throws Exception {
        return httpTool.execute(params);
    }
}
