package com.agents.builder.common.ai.strategy.impl;

import com.agents.builder.common.ai.enums.LlmProvider;
import com.agents.builder.common.ai.strategy.common.BaseChatModelBuilder;
import com.agents.builder.common.core.utils.SpringUtils;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.function.DefaultFunctionCallbackResolver;
import org.springframework.ai.model.function.FunctionCallbackResolver;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.resolution.SpringBeanToolCallbackResolver;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenaiChatModelBuilder extends BaseChatModelBuilder {


    @Override
    public LlmProvider provider() {
        return LlmProvider.OPEN_AI;
    }


    @Override
    public ChatModel getChatModel(String apiKey, String baseUrl,String modelName) {
        SpringBeanToolCallbackResolver functionCallbackResolver = new SpringBeanToolCallbackResolver(new GenericApplicationContext(),null);

        return new OpenAiChatModel(new OpenAiApi(baseUrl, (ApiKey)(new SimpleApiKey(apiKey)),  CollectionUtils.toMultiValueMap(Map.of()), "/v1/chat/completions", "/v1/embeddings", RestClient.builder(),  WebClient.builder(), RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER),
                OpenAiChatOptions.builder().model(modelName).build(),
                ToolCallingManager.builder().toolCallbackResolver(functionCallbackResolver).observationRegistry(ObservationRegistry.NOOP).toolExecutionExceptionProcessor(DefaultToolExecutionExceptionProcessor.builder().build()).build(),
                RetryUtils.DEFAULT_RETRY_TEMPLATE,
                ObservationRegistry.NOOP);
    }
}
