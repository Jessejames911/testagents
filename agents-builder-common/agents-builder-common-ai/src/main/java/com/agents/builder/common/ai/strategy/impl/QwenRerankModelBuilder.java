package com.agents.builder.common.ai.strategy.impl;

import com.agents.builder.common.ai.enums.LlmProvider;
import com.agents.builder.common.ai.strategy.common.BaseRerankModelBuilder;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankOptions;
import com.alibaba.cloud.ai.model.RerankModel;
import org.springframework.stereotype.Service;

@Service
public class QwenRerankModelBuilder extends BaseRerankModelBuilder {


    @Override
    public RerankModel getChatModel(String apiKey, String baseUrl, String modelName) {
        return new DashScopeRerankModel(new DashScopeApi(apiKey), DashScopeRerankOptions.builder().withModel(modelName).build());
    }

    @Override
    public LlmProvider provider() {
        return LlmProvider.QWEN;
    }
}
