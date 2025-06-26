package com.agents.builder.common.ai.vector;

import com.agents.builder.common.ai.strategy.EmbeddingModelBuilder;
import com.agents.builder.common.ai.strategy.context.EmbeddingModelBuilderContext;
import com.agents.builder.common.core.exception.ServiceException;
import io.micrometer.observation.ObservationRegistry;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.DescribeIndexResponse;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.DescribeIndexParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;

import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class VectorStoreBuilder {

    private final MilvusServiceClient milvusClient;

    private final MilvusVectorStoreProperties properties;

    private final BatchingStrategy batchingStrategy;

    private final org.springframework.beans.factory.ObjectProvider<ObservationRegistry> observationRegistry;

    private final ObjectProvider<VectorStoreObservationConvention> customObservationConvention;

    private final EmbeddingModelBuilderContext embeddingModelBuilderContext;

    private Map<String, CustomMilvusVectorStore> VECTOR_STORE_CACHE = new ConcurrentHashMap<>();


    public CustomMilvusVectorStore build(String provider, String modelName, String apiBase, String apiKey, String collectionName, String databaseName, boolean initialize) {
        EmbeddingModelBuilder embeddingModelBuilder = embeddingModelBuilderContext.getService(provider, () -> new ServiceException("暂不支持该厂商"));

        return build(embeddingModelBuilder.build(apiKey, apiBase, modelName), collectionName, databaseName, initialize);

    }

    public synchronized CustomMilvusVectorStore build(EmbeddingModel embeddingModel, String collectionName, String databaseName, boolean initialize) {
        CustomMilvusVectorStore vectorStore = VECTOR_STORE_CACHE.get(embeddingModel.hashCode() + collectionName + databaseName);
        if (vectorStore != null) {
            return vectorStore;
        }
        CustomMilvusVectorStore.MilvusVectorStoreConfig config
                = CustomMilvusVectorStore.MilvusVectorStoreConfig.builder()
                .withCollectionName(Optional.ofNullable(collectionName).orElse(properties.getCollectionName()))
                .withDatabaseName(Optional.ofNullable(databaseName).orElse(properties.getDatabaseName()))
                .withIndexType(IndexType.valueOf(properties.getIndexType().name()))
                .withMetricType(MetricType.valueOf(properties.getMetricType().name()))
                .withIndexParameters(properties.getIndexParameters())
                .withEmbeddingDimension(properties.getEmbeddingDimension()).build();
        CustomMilvusVectorStore milvusVectorStore = new CustomMilvusVectorStore(milvusClient, embeddingModel, config, initialize, batchingStrategy, (ObservationRegistry) observationRegistry.getIfUnique(() -> {
            return ObservationRegistry.NOOP;
        }), (VectorStoreObservationConvention) customObservationConvention.getIfAvailable(() -> {
            return null;
        }));
        VECTOR_STORE_CACHE.put(embeddingModel.hashCode() + collectionName + databaseName, milvusVectorStore);

        return milvusVectorStore;
    }

    public void clearCache() {
        VECTOR_STORE_CACHE.clear();
    }

    public void initialize(EmbeddingModel embeddingModel, String collectionName, String databaseName) {
        try {
            CustomMilvusVectorStore vectorStore = build(embeddingModel, collectionName, databaseName, true);
            VECTOR_STORE_CACHE.put(embeddingModel.hashCode() + collectionName + databaseName, vectorStore);
            vectorStore.afterPropertiesSet();
        } catch (Exception e) {
            log.error("初始化向量库失败", e);
            throw new ServiceException("初始化向量库失败");
        }
    }








    public void deleteCollection(String collectionName) {
        Boolean exist = this.milvusClient
                .hasCollection(HasCollectionParam.newBuilder()
                        .withDatabaseName(properties.getDatabaseName())
                        .withCollectionName(collectionName)
                        .build())
                .getData();
        if (!exist) {
            return;
        }
        R<RpcStatus> rpcStatusR = this.milvusClient.dropCollection(DropCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withDatabaseName(properties.getDatabaseName()).build());

        if (rpcStatusR.getException() != null) {
            log.error("删除向量库失败", rpcStatusR.getException());
            throw new ServiceException("删除向量库失败");
        }
    }


}
