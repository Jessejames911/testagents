package com.agents.builder.main.strategy.context;

import com.agents.builder.common.core.support.strategy.StrategyServiceContext;
import com.agents.builder.main.enums.DocType;
import com.agents.builder.main.strategy.DocSplitStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DocSplitContext extends StrategyServiceContext<DocSplitStrategy> {

    @Autowired
    public DocSplitContext(List<DocSplitStrategy> docSplitStrategies) {
        super(docSplitStrategies);
    }

    @Override
    protected Function<DocSplitStrategy, String> serviceKey() {
        return null;
    }

    @Override
    protected Function<DocSplitStrategy, Set<String>> bindingServiceKey() {
        return s->s.docTypes().stream()
                .map(DocType::getType)
                .collect(Collectors.toSet());
    }
}
