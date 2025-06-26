package com.agents.builder.common.script.context;

import com.agents.builder.common.core.support.strategy.StrategyServiceContext;
import com.agents.builder.common.script.ScriptExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Component
public class ScriptExecutorContext extends StrategyServiceContext<ScriptExecutor> {

    @Autowired
    public ScriptExecutorContext(List<ScriptExecutor> scriptExecutors) {
        super(scriptExecutors);
    }

    @Override
    protected Function<ScriptExecutor, String> serviceKey() {
        return s -> s.type().getKey();
    }

    @Override
    protected Function<ScriptExecutor, Set<String>> bindingServiceKey() {
        return null;
    }
}
