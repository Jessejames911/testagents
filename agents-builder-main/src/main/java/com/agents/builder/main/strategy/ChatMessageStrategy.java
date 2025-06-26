package com.agents.builder.main.strategy;

import com.agents.builder.main.domain.ChatMessage;
import com.agents.builder.main.domain.vo.ChatMessageVo;
import com.agents.builder.main.enums.AppType;
import reactor.core.publisher.Flux;

public interface ChatMessageStrategy {


    Flux<ChatMessageVo> streamChat(ChatMessage chatMessage);

    AppType type();

    ChatMessageVo chat(ChatMessage chatMessage);
}
