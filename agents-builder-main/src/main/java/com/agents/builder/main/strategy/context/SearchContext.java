package com.agents.builder.main.strategy.context;


import com.agents.builder.common.core.support.strategy.StrategyServiceContext;
import com.agents.builder.main.strategy.SearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Component
public class SearchContext extends StrategyServiceContext<SearchStrategy> {
    @Autowired
    public SearchContext(List<SearchStrategy> searchStrategies) {
        super(searchStrategies);
    }

    @Override
    protected Function<SearchStrategy, String> serviceKey() {
        return s->s.mode().getKey();
    }

    @Override
    protected Function<SearchStrategy, Set<String>> bindingServiceKey() {
        return null;
    }
}
