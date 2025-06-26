package com.agents.builder.common.ai.strategy;

import com.agents.builder.common.ai.enums.LlmProvider;
import org.springframework.ai.embedding.EmbeddingModel;

public interface EmbeddingModelBuilder {

    EmbeddingModel build(String apiKey, String baseUrl,String modelName);

    void invalidCache(String apiKey, String baseUrl,String modelName);

    LlmProvider provider();
}
