package com.agents.builder.common.script.common;

import com.agents.builder.common.script.groovy.tools.HttpTool;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public abstract class FunctionExecutor {

    @Autowired
    protected HttpTool httpTool;

    public abstract Object execute(Map<String, Object> params) throws Exception;

    /**
     * init handler, invoked when JobThread init
     */
    public void init() throws Exception {
        // do something
    }


    /**
     * destroy handler, invoked when JobThread destroy
     */
    public void destroy() throws Exception {
        // do something
    }
}
