package com.agents.builder.common.ai.strategy.common;

import com.agents.builder.common.ai.strategy.EmbeddingModelBuilder;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

public abstract class BaseEmbeddingModelBuilder implements EmbeddingModelBuilder {

    @Autowired
    protected MilvusVectorStoreProperties milvusVectorStoreProperties;

    public static Cache<String, EmbeddingModel> EMBEDDING_MODEL = Caffeine.newBuilder()
            // 设置最后一次写入或访问后经过固定时间过期
            .expireAfterWrite(24, TimeUnit.HOURS)
            // 初始的缓存空间大小
            .initialCapacity(5)
            // 缓存的最大条数
            .maximumSize(10)
            .build();

    public synchronized EmbeddingModel build(String apiKey, String baseUrl, String modelName) {
        EmbeddingModel embeddingModel = EMBEDDING_MODEL.getIfPresent(baseUrl + apiKey + modelName);
        if (embeddingModel != null) {
            return embeddingModel;
        }
        EmbeddingModel newModel = getEmbeddingModel(apiKey, baseUrl, modelName);
        EMBEDDING_MODEL.put(baseUrl + apiKey + modelName, newModel);
        return newModel;
    }

    @Override
    public void invalidCache(String apiKey, String baseUrl, String modelName) {
        String key = baseUrl + apiKey + modelName;
        EMBEDDING_MODEL.invalidate(key);
    }

    protected abstract EmbeddingModel getEmbeddingModel(String apiKey, String baseUrl, String modelName);

}
