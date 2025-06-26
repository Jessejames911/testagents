package com.agents.builder.common.ai.strategy.impl;

import com.agents.builder.common.ai.enums.LlmProvider;
import com.agents.builder.common.ai.strategy.EmbeddingModelBuilder;
import com.agents.builder.common.ai.strategy.common.BaseEmbeddingModelBuilder;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QwenEmbeddingModelBuilder extends BaseEmbeddingModelBuilder {



    public EmbeddingModel getEmbeddingModel(String apiKey, String baseUrl,String modelName) {
        return new DashScopeEmbeddingModel(new DashScopeApi(apiKey), MetadataMode.EMBED, DashScopeEmbeddingOptions.builder().withDimensions(milvusVectorStoreProperties.getEmbeddingDimension()).withModel(modelName).build());
    }

    @Override
    public LlmProvider provider() {
        return LlmProvider.QWEN;
    }
}
