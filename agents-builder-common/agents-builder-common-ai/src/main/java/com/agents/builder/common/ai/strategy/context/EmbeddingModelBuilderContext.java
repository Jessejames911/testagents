package com.agents.builder.common.ai.strategy.context;

import com.agents.builder.common.ai.strategy.ChatModelBuilder;
import com.agents.builder.common.ai.strategy.EmbeddingModelBuilder;
import com.agents.builder.common.core.support.strategy.StrategyServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Component
public class EmbeddingModelBuilderContext extends StrategyServiceContext<EmbeddingModelBuilder> {
    @Autowired
    public EmbeddingModelBuilderContext(List<EmbeddingModelBuilder> embeddingModelBuilders) {
        super(embeddingModelBuilders);
    }

    @Override
    protected Function<EmbeddingModelBuilder, String> serviceKey() {
        return s->s.provider().getKey();
    }

    @Override
    protected Function<EmbeddingModelBuilder, Set<String>> bindingServiceKey() {
        return null;
    }
}
