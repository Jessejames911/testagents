package com.agents.builder.common.ai.strategy.impl;

import com.agents.builder.common.ai.enums.LlmProvider;
import com.agents.builder.common.ai.strategy.common.BaseChatModelBuilder;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

@Service
public class OllamaChatModelBuilder extends BaseChatModelBuilder {


    @Override
    public LlmProvider provider() {
        return LlmProvider.OLLAMA;
    }


    @Override
    public ChatModel getChatModel(String apiKey, String baseUrl,String modelName) {
        return OllamaChatModel.builder().ollamaApi(new OllamaApi(baseUrl))
                .defaultOptions(OllamaOptions.builder().model(modelName).build()).build();
    }
}
