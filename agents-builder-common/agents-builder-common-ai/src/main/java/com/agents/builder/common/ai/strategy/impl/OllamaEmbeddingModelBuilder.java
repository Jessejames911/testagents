package com.agents.builder.common.ai.strategy.impl;

import com.agents.builder.common.ai.enums.LlmProvider;
import com.agents.builder.common.ai.strategy.common.BaseEmbeddingModelBuilder;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

@Service
public class OllamaEmbeddingModelBuilder extends BaseEmbeddingModelBuilder {
    @Override
    public LlmProvider provider() {
        return LlmProvider.OLLAMA;
    }

    @Override
    protected EmbeddingModel getEmbeddingModel(String apiKey, String baseUrl,String modelName) {
        return OllamaEmbeddingModel.builder().ollamaApi(new OllamaApi(baseUrl))
                .defaultOptions(OllamaOptions.builder().model(modelName).build())
                .build();
    }
}
