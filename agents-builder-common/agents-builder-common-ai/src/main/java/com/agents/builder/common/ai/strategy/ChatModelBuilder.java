package com.agents.builder.common.ai.strategy;

import com.agents.builder.common.ai.enums.LlmProvider;

import com.agents.builder.common.ai.enums.ModelType;
import org.springframework.ai.chat.model.ChatModel;

import java.util.Set;

public interface ChatModelBuilder {

    ChatModel build(String apiKey, String baseUrl,String modelName);

    void invalidCache(String apiKey, String baseUrl,String modelName);

    LlmProvider provider();

}
