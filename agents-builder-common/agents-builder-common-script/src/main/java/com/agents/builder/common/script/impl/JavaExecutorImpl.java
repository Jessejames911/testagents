package com.agents.builder.common.script.impl;

import com.agents.builder.common.script.common.CommonScriptExecutor;
import com.agents.builder.common.script.common.FunctionExecutor;
import com.agents.builder.common.script.common.GroovyExecuteHandler;
import com.agents.builder.common.script.enums.ScriptLanguage;
import com.agents.builder.common.script.groovy.GroovyFactory;
import com.agents.builder.common.script.groovy.SpringGroovyFactory;
import groovy.lang.GroovyShell;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JavaExecutorImpl  extends CommonScriptExecutor {


    @Override
    public ScriptLanguage type() {
        return ScriptLanguage.JAVA;
    }

    @Override
    public Object execute(String script, Map<String, Object> params) throws Exception {
        if (!script.contains(FunctionExecutor.class.getName())){
            GroovyShell groovyShell = new GroovyShell();
            params.forEach(groovyShell::setVariable);
            return groovyShell.evaluate(script);
        }
        GroovyFactory.refreshInstance(1);
        FunctionExecutor functionExecutor = SpringGroovyFactory.getInstance().loadNewInstance(script);
        GroovyExecuteHandler executeHandler = new GroovyExecuteHandler(functionExecutor);
        executeHandler.init();
        Object result = executeHandler.execute(params);
        executeHandler.destroy();
        return result;
    }

    @Override
    public Object compile(String script) throws Exception {
        return null;
    }
}
