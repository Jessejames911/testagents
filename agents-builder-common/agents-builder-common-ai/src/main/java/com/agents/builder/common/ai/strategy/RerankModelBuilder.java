package com.agents.builder.common.ai.strategy;

import com.agents.builder.common.ai.enums.LlmProvider;
import com.alibaba.cloud.ai.model.RerankModel;

public interface RerankModelBuilder {

    RerankModel build(String apiKey, String baseUrl, String modelName);

    void invalidCache(String apiKey, String baseUrl,String modelName);

    LlmProvider provider();
}
