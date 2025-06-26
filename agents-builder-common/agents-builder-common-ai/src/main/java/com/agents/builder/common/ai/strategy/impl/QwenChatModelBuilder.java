package com.agents.builder.common.ai.strategy.impl;

import com.agents.builder.common.ai.enums.LlmProvider;
import com.agents.builder.common.ai.strategy.common.BaseChatModelBuilder;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QwenChatModelBuilder extends BaseChatModelBuilder {



    @Override
    public LlmProvider provider() {
        return LlmProvider.QWEN;
    }



    @Override
    public ChatModel getChatModel(String apiKey, String baseUrl,String modelName) {
        return new DashScopeChatModel(new DashScopeApi(apiKey), DashScopeChatOptions.builder().withModel(modelName).build());
    }
}
