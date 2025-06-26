package com.agents.builder.common.core.workflow.compare;

import com.agents.builder.common.core.support.strategy.StrategyServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Component
public class CompareContext  extends StrategyServiceContext<Compare> {

    @Autowired
    public CompareContext(List<Compare> compares) {
        super(compares);
    }

    @Override
    protected Function<Compare, String> serviceKey() {
        return s -> s.type().getKey();
    }

    @Override
    protected Function<Compare, Set<String>> bindingServiceKey() {
        return null;
    }
}
