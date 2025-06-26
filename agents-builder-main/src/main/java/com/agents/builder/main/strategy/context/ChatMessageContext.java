package com.agents.builder.main.strategy.context;

import com.agents.builder.common.core.support.strategy.StrategyServiceContext;
import com.agents.builder.main.strategy.ChatMessageStrategy;
import com.agents.builder.main.strategy.DocSplitStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Component
public class ChatMessageContext extends StrategyServiceContext<ChatMessageStrategy> {
    @Autowired
    public ChatMessageContext(List<ChatMessageStrategy> chatMessageStrategies) {
        super(chatMessageStrategies);
    }

    @Override
    protected Function<ChatMessageStrategy, String> serviceKey() {
        return s->s.type().getKey();
    }

    @Override
    protected Function<ChatMessageStrategy, Set<String>> bindingServiceKey() {
        return null;
    }
}
