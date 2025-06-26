package com.agents.builder.common.ai.strategy.common;

import com.agents.builder.common.ai.strategy.ChatModelBuilder;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.ai.chat.model.ChatModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public abstract class BaseChatModelBuilder implements ChatModelBuilder {

    public static Cache<String, ChatModel> CHAT_MODEL_CACHE = Caffeine.newBuilder()
            // 设置最后一次写入或访问后经过固定时间过期
            .expireAfterWrite(24, TimeUnit.HOURS)
            // 初始的缓存空间大小
            .initialCapacity(20)
            // 缓存的最大条数
            .maximumSize(50)
            .build();

    public synchronized ChatModel build(String apiKey, String baseUrl, String modelName) {
        ChatModel chatModel = CHAT_MODEL_CACHE.getIfPresent(baseUrl + apiKey + modelName);
        if (chatModel != null) {
            return chatModel;
        }
        ChatModel newChatModel = getChatModel(apiKey, baseUrl, modelName);
        CHAT_MODEL_CACHE.put(baseUrl + apiKey + modelName, newChatModel);
        return newChatModel;
    }

    @Override
    public void invalidCache(String apiKey, String baseUrl, String modelName) {
        String key = baseUrl + apiKey + modelName;
        CHAT_MODEL_CACHE.invalidate(key);
    }

    public abstract ChatModel getChatModel(String apiKey, String baseUrl, String modelName);
}
