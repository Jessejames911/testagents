package com.agents.builder.common.ai.strategy.common;

import com.agents.builder.common.ai.strategy.RerankModelBuilder;
import com.alibaba.cloud.ai.model.RerankModel;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.ai.chat.model.ChatModel;

import java.util.concurrent.TimeUnit;

public abstract class BaseRerankModelBuilder implements RerankModelBuilder {

    public static Cache<String, RerankModel> RERANK_MODEL_CACHE = Caffeine.newBuilder()
            // 设置最后一次写入或访问后经过固定时间过期
            .expireAfterWrite(24, TimeUnit.HOURS)
            // 初始的缓存空间大小
            .initialCapacity(20)
            // 缓存的最大条数
            .maximumSize(50)
            .build();

    public synchronized RerankModel build(String apiKey, String baseUrl, String modelName) {
        RerankModel rerankModel = RERANK_MODEL_CACHE.getIfPresent(baseUrl + apiKey + modelName);
        if (rerankModel != null) {
            return rerankModel;
        }
        RerankModel newRerankModel = getChatModel(apiKey, baseUrl, modelName);
        RERANK_MODEL_CACHE.put(baseUrl + apiKey + modelName, newRerankModel);
        return newRerankModel;
    }

    @Override
    public void invalidCache(String apiKey, String baseUrl, String modelName) {
        String key = baseUrl + apiKey + modelName;
        RERANK_MODEL_CACHE.invalidate(key);
    }

    public abstract RerankModel getChatModel(String apiKey, String baseUrl, String modelName);
}
