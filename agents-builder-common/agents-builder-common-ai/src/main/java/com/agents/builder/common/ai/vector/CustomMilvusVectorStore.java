package com.agents.builder.common.ai.vector;


import com.alibaba.fastjson.JSONObject;
import io.micrometer.observation.ObservationRegistry;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.exception.ParamException;
import io.milvus.grpc.DataType;
import io.milvus.grpc.DescribeIndexResponse;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.DescribeIndexParam;
import io.milvus.param.index.DropIndexParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentMetadata;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingOptionsBuilder;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.model.EmbeddingUtils;
import org.springframework.ai.observation.conventions.VectorStoreProvider;
import org.springframework.ai.observation.conventions.VectorStoreSimilarityMetric;
import org.springframework.ai.vectorstore.AbstractVectorStoreBuilder;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionConverter;
import org.springframework.ai.vectorstore.milvus.MilvusFilterExpressionConverter;

import org.springframework.ai.vectorstore.observation.AbstractObservationVectorStore;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationContext;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class CustomMilvusVectorStore extends AbstractObservationVectorStore implements InitializingBean {


    private static final Logger logger = LoggerFactory.getLogger(CustomMilvusVectorStore.class);

    public static final int OPENAI_EMBEDDING_DIMENSION_SIZE = 1536;

    public static final int INVALID_EMBEDDING_DIMENSION = -1;

    public static final String DEFAULT_DATABASE_NAME = "default";

    public static final String DEFAULT_COLLECTION_NAME = "vector_store";

    public static final String DOC_ID_FIELD_NAME = "doc_id";

    public static final String CONTENT_FIELD_NAME = "content";

    public static final String METADATA_FIELD_NAME = "metadata";

    public static final String EMBEDDING_FIELD_NAME = "embedding";

    // Metadata, automatically assigned by Milvus.
    public static final String DISTANCE_FIELD_NAME = "distance";


    private static final Map<MetricType, VectorStoreSimilarityMetric> SIMILARITY_TYPE_MAPPING = Map.of(
            MetricType.COSINE, VectorStoreSimilarityMetric.COSINE, MetricType.L2, VectorStoreSimilarityMetric.EUCLIDEAN,
            MetricType.IP, VectorStoreSimilarityMetric.DOT);

    public final FilterExpressionConverter filterExpressionConverter = new MilvusFilterExpressionConverter();

    private final MilvusServiceClient milvusClient;

    @Deprecated(forRemoval = true, since = "1.0.0-M5")
    private final CustomMilvusVectorStore.MilvusVectorStoreConfig config;

    private final boolean initializeSchema;

    private final BatchingStrategy batchingStrategy;

    private final String databaseName;

    private final String collectionName;

    private final int embeddingDimension;

    private final IndexType indexType;

    private final MetricType metricType;

    private final String indexParameters;

    private final String idFieldName;

    private final boolean isAutoId;

    private final String contentFieldName;

    private final String metadataFieldName;

    private final String embeddingFieldName;

    @Deprecated(forRemoval = true, since = "1.0.0-M5")
    public CustomMilvusVectorStore(MilvusServiceClient milvusClient, EmbeddingModel embeddingModel,
                             boolean initializeSchema) {
        this(milvusClient, embeddingModel, CustomMilvusVectorStore.MilvusVectorStoreConfig.defaultConfig(), initializeSchema,
                new TokenCountBatchingStrategy());
    }

    @Deprecated(forRemoval = true, since = "1.0.0-M5")
    public CustomMilvusVectorStore(MilvusServiceClient milvusClient, EmbeddingModel embeddingModel, boolean initializeSchema,
                             BatchingStrategy batchingStrategy) {
        this(milvusClient, embeddingModel, CustomMilvusVectorStore.MilvusVectorStoreConfig.defaultConfig(), initializeSchema, batchingStrategy);
    }

    @Deprecated(forRemoval = true, since = "1.0.0-M5")
    public CustomMilvusVectorStore(MilvusServiceClient milvusClient, EmbeddingModel embeddingModel,
                             CustomMilvusVectorStore.MilvusVectorStoreConfig config, boolean initializeSchema, BatchingStrategy batchingStrategy) {
        this(milvusClient, embeddingModel, config, initializeSchema, batchingStrategy, ObservationRegistry.NOOP, null);
    }

    @Deprecated(forRemoval = true, since = "1.0.0-M5")
    public CustomMilvusVectorStore(MilvusServiceClient milvusClient, EmbeddingModel embeddingModel,
                             CustomMilvusVectorStore.MilvusVectorStoreConfig config, boolean initializeSchema, BatchingStrategy batchingStrategy,
                             ObservationRegistry observationRegistry, VectorStoreObservationConvention customObservationConvention) {

        this(builder(milvusClient, embeddingModel).observationRegistry(observationRegistry)
                .customObservationConvention(customObservationConvention)
                .initializeSchema(initializeSchema)
                .batchingStrategy(batchingStrategy));
    }

    /**
     * @param builder {@link VectorStore.Builder} for chroma vector store
     */
    protected CustomMilvusVectorStore(CustomMilvusVectorStore.Builder builder) {
        super(builder);

        Assert.notNull(builder.milvusClient, "milvusClient must not be null");

        this.milvusClient = builder.milvusClient;
        this.batchingStrategy = builder.batchingStrategy;
        this.initializeSchema = builder.initializeSchema;
        this.config = null;
        this.databaseName = builder.databaseName;
        this.collectionName = builder.collectionName;
        this.embeddingDimension = builder.embeddingDimension;
        this.indexType = builder.indexType;
        this.metricType = builder.metricType;
        this.indexParameters = builder.indexParameters;
        this.idFieldName = builder.idFieldName;
        this.isAutoId = builder.isAutoId;
        this.contentFieldName = builder.contentFieldName;
        this.metadataFieldName = builder.metadataFieldName;
        this.embeddingFieldName = builder.embeddingFieldName;
    }

    /**
     * Creates a new MilvusBuilder instance with the specified Milvus client. This is the
     * recommended way to instantiate a MilvusBuilder.
     * @return a new MilvusBuilder instance
     */
    public static CustomMilvusVectorStore.Builder builder(MilvusServiceClient milvusServiceClient, EmbeddingModel embeddingModel) {
        return new CustomMilvusVectorStore.Builder(milvusServiceClient, embeddingModel);
    }

    @Override
    public void doAdd(List<Document> documents) {

        Assert.notNull(documents, "Documents must not be null");

        List<String> docIdArray = new ArrayList<>();
        List<String> contentArray = new ArrayList<>();
        List<JSONObject> metadataArray = new ArrayList<>();
        List<List<Float>> embeddingArray = new ArrayList<>();

        // TODO: Need to customize how we pass the embedding options
        List<float[]> embeddings = this.embeddingModel.embed(documents, EmbeddingOptionsBuilder.builder().build(),
                this.batchingStrategy);

        for (Document document : documents) {
            docIdArray.add(document.getId());
            // Use a (future) DocumentTextLayoutFormatter instance to extract
            // the content used to compute the embeddings
            contentArray.add(document.getText());
            metadataArray.add(new JSONObject(document.getMetadata()));
            embeddingArray.add(EmbeddingUtils.toList(embeddings.get(documents.indexOf(document))));
        }

        List<InsertParam.Field> fields = new ArrayList<>();
        // Insert ID field only if it is not auto ID
        if (!this.isAutoId) {
            fields.add(new InsertParam.Field(this.idFieldName, docIdArray));
        }
        fields.add(new InsertParam.Field(this.contentFieldName, contentArray));
        fields.add(new InsertParam.Field(this.metadataFieldName, metadataArray));
        fields.add(new InsertParam.Field(this.embeddingFieldName, embeddingArray));

        InsertParam insertParam = InsertParam.newBuilder()
                .withDatabaseName(this.databaseName)
                .withCollectionName(this.collectionName)
                .withFields(fields)
                .build();

        R<MutationResult> status = this.milvusClient.insert(insertParam);
        if (status.getException() != null) {
            throw new RuntimeException("Failed to insert:", status.getException());
        }
    }

    @Override
    public void doDelete(List<String> idList) {
        Assert.notNull(idList, "Document id list must not be null");
        String deleteExpression = String.format("%s in [%s]", this.idFieldName, idList.stream().map((id) -> {
            return "'" + id + "'";
        }).collect(Collectors.joining(",")));
        R<MutationResult> status = this.milvusClient.delete(DeleteParam.newBuilder().withDatabaseName(this.databaseName).withCollectionName(this.collectionName).withExpr(deleteExpression).build());
        long deleteCount = ((MutationResult)status.getData()).getDeleteCnt();
        if (deleteCount != (long)idList.size()) {
            logger.warn(String.format("Deleted only %s entries from requested %s ", deleteCount, idList.size()));
        }
    }

    @Override
    public List<Document> doSimilaritySearch(SearchRequest request) {

        String nativeFilterExpressions = (request.getFilterExpression() != null)
                ? this.filterExpressionConverter.convertExpression(request.getFilterExpression()) : "";

        Assert.notNull(request.getQuery(), "Query string must not be null");
        List<String> outFieldNames = new ArrayList<>();
        outFieldNames.add(this.idFieldName);
        outFieldNames.add(this.contentFieldName);
        outFieldNames.add(this.metadataFieldName);
        float[] embedding = this.embeddingModel.embed(request.getQuery());

        var searchParamBuilder = SearchParam.newBuilder()
                .withDatabaseName(this.databaseName)
                .withCollectionName(this.collectionName)
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .withMetricType(this.metricType)
                .withOutFields(outFieldNames)
                .withTopK(request.getTopK())
                .withVectors(List.of(EmbeddingUtils.toList(embedding)))
                .withVectorFieldName(this.embeddingFieldName);

        if (StringUtils.hasText(nativeFilterExpressions)) {
            searchParamBuilder.withExpr(nativeFilterExpressions);
        }

        R<SearchResults> respSearch = this.milvusClient.search(searchParamBuilder.build());

        if (respSearch.getException() != null) {
            throw new RuntimeException("Search failed!", respSearch.getException());
        }

        SearchResultsWrapper wrapperSearch = new SearchResultsWrapper(respSearch.getData().getResults());

        return wrapperSearch.getRowRecords(0)
                .stream()
                .filter(rowRecord -> getResultSimilarity(rowRecord) >= request.getSimilarityThreshold())
                .map(rowRecord -> {
                    String docId = String.valueOf(rowRecord.get(this.idFieldName));
                    String content = (String) rowRecord.get(this.contentFieldName);
                    JSONObject metadata = null;
                    try {
                        metadata = (JSONObject) rowRecord.get(this.metadataFieldName);
                        // inject the distance into the metadata.
                        metadata.put(DocumentMetadata.DISTANCE.value(), 1 - getResultSimilarity(rowRecord));
                    }
                    catch (ParamException e) {
                        // skip the ParamException if metadata doesn't exist for the custom
                        // collection
                    }
                    return Document.builder()
                            .id(docId)
                            .text(content)
                            .metadata((metadata != null) ? metadata.getInnerMap() : Map.of())
                            .score((double) getResultSimilarity(rowRecord))
                            .build();
                })
                .toList();
    }

    private float getResultSimilarity(QueryResultsWrapper.RowRecord rowRecord) {
        Float distance = (Float) rowRecord.get(DISTANCE_FIELD_NAME);
        return (this.metricType == MetricType.IP || this.metricType == MetricType.COSINE) ? distance : (1 - distance);
    }

    // ---------------------------------------------------------------------------------
    // Initialization
    // ---------------------------------------------------------------------------------
    @Override
    public void afterPropertiesSet() throws Exception {

        if (!this.initializeSchema) {
            return;
        }

        this.createCollection();
    }

    void releaseCollection() {
        if (isDatabaseCollectionExists()) {
            this.milvusClient
                    .releaseCollection(ReleaseCollectionParam.newBuilder().withCollectionName(this.collectionName).build());
        }
    }

    private boolean isDatabaseCollectionExists() {
        return this.milvusClient
                .hasCollection(HasCollectionParam.newBuilder()
                        .withDatabaseName(this.databaseName)
                        .withCollectionName(this.collectionName)
                        .build())
                .getData();
    }

    // used by the test as well
    void createCollection() {

        if (!isDatabaseCollectionExists()) {
            createCollection(this.databaseName, this.collectionName, this.idFieldName, this.isAutoId,
                    this.contentFieldName, this.metadataFieldName, this.embeddingFieldName);
        }

        R<DescribeIndexResponse> indexDescriptionResponse = this.milvusClient
                .describeIndex(DescribeIndexParam.newBuilder()
                        .withDatabaseName(this.databaseName)
                        .withCollectionName(this.collectionName)
                        .build());

        if (indexDescriptionResponse.getData() == null) {
            R<RpcStatus> indexStatus = this.milvusClient.createIndex(CreateIndexParam.newBuilder()
                    .withDatabaseName(this.databaseName)
                    .withCollectionName(this.collectionName)
                    .withFieldName(this.embeddingFieldName)
                    .withIndexType(this.indexType)
                    .withMetricType(this.metricType)
                    .withExtraParam(this.indexParameters)
                    .withSyncMode(Boolean.FALSE)
                    .build());

            if (indexStatus.getException() != null) {
                throw new RuntimeException("Failed to create Index", indexStatus.getException());
            }
        }

        R<RpcStatus> loadCollectionStatus = this.milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                .withDatabaseName(this.databaseName)
                .withCollectionName(this.collectionName)
                .build());

        if (loadCollectionStatus.getException() != null) {
            throw new RuntimeException("Collection loading failed!", loadCollectionStatus.getException());
        }
    }

    void createCollection(String databaseName, String collectionName, String idFieldName, boolean isAutoId,
                          String contentFieldName, String metadataFieldName, String embeddingFieldName) {
        FieldType docIdFieldType = FieldType.newBuilder()
                .withName(idFieldName)
                .withDataType(DataType.VarChar)
                .withMaxLength(36)
                .withPrimaryKey(true)
                .withAutoID(isAutoId)
                .build();
        FieldType contentFieldType = FieldType.newBuilder()
                .withName(contentFieldName)
                .withDataType(DataType.VarChar)
                .withMaxLength(65535)
                .build();
        FieldType metadataFieldType = FieldType.newBuilder()
                .withName(metadataFieldName)
                .withDataType(DataType.JSON)
                .build();
        FieldType embeddingFieldType = FieldType.newBuilder()
                .withName(embeddingFieldName)
                .withDataType(DataType.FloatVector)
                .withDimension(this.embeddingDimensions())
                .build();

        CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
                .withDatabaseName(databaseName)
                .withCollectionName(collectionName)
                .withDescription("Spring AI Vector Store")
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .withShardsNum(2)
                .addFieldType(docIdFieldType)
                .addFieldType(contentFieldType)
                .addFieldType(metadataFieldType)
                .addFieldType(embeddingFieldType)
                .build();

        R<RpcStatus> collectionStatus = this.milvusClient.createCollection(createCollectionReq);
        if (collectionStatus.getException() != null) {
            throw new RuntimeException("Failed to create collection", collectionStatus.getException());
        }

    }

    int embeddingDimensions() {
        if (this.embeddingDimension != INVALID_EMBEDDING_DIMENSION) {
            return this.embeddingDimension;
        }
        try {
            int embeddingDimensions = this.embeddingModel.dimensions();
            if (embeddingDimensions > 0) {
                return embeddingDimensions;
            }
        }
        catch (Exception e) {
            logger.warn("Failed to obtain the embedding dimensions from the embedding model and fall backs to default:"
                    + this.embeddingDimension, e);
        }
        return OPENAI_EMBEDDING_DIMENSION_SIZE;
    }

    // used by the test as well
    void dropCollection() {

        R<RpcStatus> status = this.milvusClient
                .releaseCollection(ReleaseCollectionParam.newBuilder().withCollectionName(this.collectionName).build());

        if (status.getException() != null) {
            throw new RuntimeException("Release collection failed!", status.getException());
        }

        status = this.milvusClient
                .dropIndex(DropIndexParam.newBuilder().withCollectionName(this.collectionName).build());

        if (status.getException() != null) {
            throw new RuntimeException("Drop Index failed!", status.getException());
        }

        status = this.milvusClient.dropCollection(DropCollectionParam.newBuilder()
                .withDatabaseName(this.databaseName)
                .withCollectionName(this.collectionName)
                .build());

        if (status.getException() != null) {
            throw new RuntimeException("Drop Collection failed!", status.getException());
        }
    }

    @Override
    public org.springframework.ai.vectorstore.observation.VectorStoreObservationContext.Builder createObservationContextBuilder(
            String operationName) {

        return VectorStoreObservationContext.builder(VectorStoreProvider.MILVUS.value(), operationName)
                .collectionName(this.collectionName)
                .dimensions(this.embeddingModel.dimensions())
                .similarityMetric(getSimilarityMetric())
                .namespace(this.databaseName);
    }

    private String getSimilarityMetric() {
        if (!SIMILARITY_TYPE_MAPPING.containsKey(this.metricType)) {
            return this.metricType.name();
        }
        return SIMILARITY_TYPE_MAPPING.get(this.metricType).value();
    }

    public static class Builder extends AbstractVectorStoreBuilder<CustomMilvusVectorStore.Builder> {

        private final MilvusServiceClient milvusClient;

        private String databaseName = DEFAULT_DATABASE_NAME;

        private String collectionName = DEFAULT_COLLECTION_NAME;

        private int embeddingDimension = INVALID_EMBEDDING_DIMENSION;

        private IndexType indexType = IndexType.IVF_FLAT;

        private MetricType metricType = MetricType.COSINE;

        private String indexParameters = "{\"nlist\":1024}";

        private String idFieldName = DOC_ID_FIELD_NAME;

        private boolean isAutoId = false;

        private String contentFieldName = CONTENT_FIELD_NAME;

        private String metadataFieldName = METADATA_FIELD_NAME;

        private String embeddingFieldName = EMBEDDING_FIELD_NAME;

        private boolean initializeSchema = false;

        private BatchingStrategy batchingStrategy = new TokenCountBatchingStrategy();

        /**
         * @param milvusClient the Milvus service client to use for database operations
         * @throws IllegalArgumentException if milvusClient is null
         */
        private Builder(MilvusServiceClient milvusClient, EmbeddingModel embeddingModel) {
            super(embeddingModel);
            Assert.notNull(milvusClient, "milvusClient must not be null");
            this.milvusClient = milvusClient;
        }

        /**
         * Configures the Milvus metric type to use for similarity calculations. See:
         * https://milvus.io/docs/metric.md#floating for details on metric types.
         * @param metricType the metric type to use (IP, L2, or COSINE)
         * @return this builder instance
         * @throws IllegalArgumentException if metricType is null or not one of IP, L2, or
         * COSINE
         */
        public CustomMilvusVectorStore.Builder metricType(MetricType metricType) {
            Assert.notNull(metricType, "Collection Name must not be empty");
            Assert.isTrue(metricType == MetricType.IP || metricType == MetricType.L2 || metricType == MetricType.COSINE,
                    "Only the text metric types IP and L2 are supported");
            this.metricType = metricType;
            return this;
        }

        /**
         * Configures the Milvus index type to use for vector search optimization.
         * @param indexType the index type to use (defaults to IVF_FLAT if not specified)
         * @return this builder instance
         */
        public CustomMilvusVectorStore.Builder indexType(IndexType indexType) {
            this.indexType = indexType;
            return this;
        }

        /**
         * Configures the Milvus index parameters as a JSON string.
         * @param indexParameters the index parameters to use (defaults to {"nlist":1024}
         * if not specified)
         * @return this builder instance
         */
        public CustomMilvusVectorStore.Builder indexParameters(String indexParameters) {
            this.indexParameters = indexParameters;
            return this;
        }

        /**
         * Configures the Milvus database name.
         * @param databaseName the database name to use (defaults to DEFAULT_DATABASE_NAME
         * if not specified)
         * @return this builder instance
         */
        public CustomMilvusVectorStore.Builder databaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        /**
         * Configures the Milvus collection name.
         * @param collectionName the collection name to use (defaults to
         * DEFAULT_COLLECTION_NAME if not specified)
         * @return this builder instance
         */
        public CustomMilvusVectorStore.Builder collectionName(String collectionName) {
            this.collectionName = collectionName;
            return this;
        }

        /**
         * Configures the dimension size of the embedding vectors.
         * @param newEmbeddingDimension The dimension of the embedding (must be between 1
         * and 32768)
         * @return this builder instance
         * @throws IllegalArgumentException if dimension is not between 1 and 32768
         */
        public CustomMilvusVectorStore.Builder embeddingDimension(int newEmbeddingDimension) {
            Assert.isTrue(newEmbeddingDimension >= 1 && newEmbeddingDimension <= 32768,
                    "Dimension has to be withing the boundaries 1 and 32768 (inclusively)");
            this.embeddingDimension = newEmbeddingDimension;
            return this;
        }

        /**
         * Configures the name of the field used for document IDs.
         * @param idFieldName The name for the ID field (defaults to DOC_ID_FIELD_NAME)
         * @return this builder instance
         */
        public CustomMilvusVectorStore.Builder iDFieldName(String idFieldName) {
            this.idFieldName = idFieldName;
            return this;
        }

        /**
         * Configures whether to use auto-generated IDs for documents.
         * @param isAutoId true to enable auto-generated IDs, false to use provided IDs
         * @return this builder instance
         */
        public CustomMilvusVectorStore.Builder autoId(boolean isAutoId) {
            this.isAutoId = isAutoId;
            return this;
        }

        /**
         * Configures the name of the field used for document content.
         * @param contentFieldName The name for the content field (defaults to
         * CONTENT_FIELD_NAME)
         * @return this builder instance
         */
        public CustomMilvusVectorStore.Builder contentFieldName(String contentFieldName) {
            this.contentFieldName = contentFieldName;
            return this;
        }

        /**
         * Configures the name of the field used for document metadata.
         * @param metadataFieldName The name for the metadata field (defaults to
         * METADATA_FIELD_NAME)
         * @return this builder instance
         */
        public CustomMilvusVectorStore.Builder metadataFieldName(String metadataFieldName) {
            this.metadataFieldName = metadataFieldName;
            return this;
        }

        /**
         * Configures the name of the field used for embedding vectors.
         * @param embeddingFieldName The name for the embedding field (defaults to
         * EMBEDDING_FIELD_NAME)
         * @return this builder instance
         */
        public CustomMilvusVectorStore.Builder embeddingFieldName(String embeddingFieldName) {
            this.embeddingFieldName = embeddingFieldName;
            return this;
        }

        /**
         * Configures whether to initialize the collection schema automatically.
         * @param initializeSchema true to initialize schema automatically, false to use
         * existing schema
         * @return this builder instance
         */
        public CustomMilvusVectorStore.Builder initializeSchema(boolean initializeSchema) {
            this.initializeSchema = initializeSchema;
            return this;
        }

        /**
         * Configures the strategy for batching operations.
         * @param batchingStrategy the batching strategy to use for grouping operations
         * @return this builder instance
         * @throws IllegalArgumentException if batchingStrategy is null
         */
        public CustomMilvusVectorStore.Builder batchingStrategy(BatchingStrategy batchingStrategy) {
            Assert.notNull(batchingStrategy, "batchingStrategy must not be null");
            this.batchingStrategy = batchingStrategy;
            return this;
        }

        /**
         * Builds and returns a new CustomMilvusVectorStore instance with the configured
         * settings.
         * @return a new CustomMilvusVectorStore instance
         * @throws IllegalStateException if the builder configuration is invalid
         */
        public CustomMilvusVectorStore build() {
            return new CustomMilvusVectorStore(this);
        }

    }

    @Deprecated(forRemoval = true, since = "1.0.0-M5")
    public static final class MilvusVectorStoreConfig {

        private final String databaseName;

        private final String collectionName;

        private final int embeddingDimension;

        private final IndexType indexType;

        private final MetricType metricType;

        private final String indexParameters;

        private final String idFieldName;

        private final boolean isAutoId;

        private final String contentFieldName;

        private final String metadataFieldName;

        private final String embeddingFieldName;

        private MilvusVectorStoreConfig(CustomMilvusVectorStore.MilvusVectorStoreConfig.Builder builder) {
            this.databaseName = builder.databaseName;
            this.collectionName = builder.collectionName;
            this.embeddingDimension = builder.embeddingDimension;
            this.indexType = builder.indexType;
            this.metricType = builder.metricType;
            this.indexParameters = builder.indexParameters;
            this.idFieldName = builder.idFieldName;
            this.isAutoId = builder.isAutoId;
            this.contentFieldName = builder.contentFieldName;
            this.metadataFieldName = builder.metadataFieldName;
            this.embeddingFieldName = builder.embeddingFieldName;
        }

        /**
         * Start building a new configuration.
         * @return The entry point for creating a new configuration.
         */
        public static CustomMilvusVectorStore.MilvusVectorStoreConfig.Builder builder() {
            return new CustomMilvusVectorStore.MilvusVectorStoreConfig.Builder();
        }

        /**
         * {@return the default config}
         */
        public static CustomMilvusVectorStore.MilvusVectorStoreConfig defaultConfig() {
            return builder().build();
        }

        @Deprecated(forRemoval = true, since = "1.0.0-M5")
        public static final class Builder {

            private String databaseName = DEFAULT_DATABASE_NAME;

            private String collectionName = DEFAULT_COLLECTION_NAME;

            private int embeddingDimension = INVALID_EMBEDDING_DIMENSION;

            private IndexType indexType = IndexType.IVF_FLAT;

            private MetricType metricType = MetricType.COSINE;

            private String indexParameters = "{\"nlist\":1024}";

            private String idFieldName = DOC_ID_FIELD_NAME;

            private boolean isAutoId = false;

            private String contentFieldName = CONTENT_FIELD_NAME;

            private String metadataFieldName = METADATA_FIELD_NAME;

            private String embeddingFieldName = EMBEDDING_FIELD_NAME;

            private Builder() {
            }

            /**
             * Configures the Milvus metric type to use. Leave {@literal null} or blank to
             * use the metric metric: https://milvus.io/docs/metric.md#floating
             * @param metricType the metric type to use
             * @return this builder
             */
            public CustomMilvusVectorStore.MilvusVectorStoreConfig.Builder withMetricType(MetricType metricType) {
                Assert.notNull(metricType, "Collection Name must not be empty");
                Assert.isTrue(
                        metricType == MetricType.IP || metricType == MetricType.L2 || metricType == MetricType.COSINE,
                        "Only the text metric types IP and L2 are supported");

                this.metricType = metricType;
                return this;
            }

            /**
             * Configures the Milvus index type to use. Leave {@literal null} or blank to
             * use the default index.
             * @param indexType the index type to use
             * @return this builder
             */
            public CustomMilvusVectorStore.MilvusVectorStoreConfig.Builder withIndexType(IndexType indexType) {
                this.indexType = indexType;
                return this;
            }

            /**
             * Configures the Milvus index parameters to use. Leave {@literal null} or
             * blank to use the default index parameters.
             * @param indexParameters the index parameters to use
             * @return this builder
             */
            public CustomMilvusVectorStore.MilvusVectorStoreConfig.Builder withIndexParameters(String indexParameters) {
                this.indexParameters = indexParameters;
                return this;
            }

            /**
             * Configures the Milvus database name to use. Leave {@literal null} or blank
             * to use the default database.
             * @param databaseName the database name to use
             * @return this builder
             */
            public CustomMilvusVectorStore.MilvusVectorStoreConfig.Builder withDatabaseName(String databaseName) {
                this.databaseName = databaseName;
                return this;
            }

            /**
             * Configures the Milvus collection name to use. Leave {@literal null} or
             * blank to use the default collection name.
             * @param collectionName the collection name to use
             * @return this builder
             */
            public CustomMilvusVectorStore.MilvusVectorStoreConfig.Builder withCollectionName(String collectionName) {
                this.collectionName = collectionName;
                return this;
            }

            /**
             * Configures the size of the embedding. Defaults to {@literal 1536}, inline
             * with OpenAIs embeddings.
             * @param newEmbeddingDimension The dimension of the embedding
             * @return this builder
             */
            public CustomMilvusVectorStore.MilvusVectorStoreConfig.Builder withEmbeddingDimension(int newEmbeddingDimension) {

                Assert.isTrue(newEmbeddingDimension >= 1 && newEmbeddingDimension <= 32768,
                        "Dimension has to be withing the boundaries 1 and 32768 (inclusively)");

                this.embeddingDimension = newEmbeddingDimension;
                return this;
            }

            /**
             * Configures the ID field name. Default is {@value #DOC_ID_FIELD_NAME}.
             * @param idFieldName The name for the ID field
             * @return this builder
             */
            public CustomMilvusVectorStore.MilvusVectorStoreConfig.Builder withIDFieldName(String idFieldName) {
                this.idFieldName = idFieldName;
                return this;
            }

            /**
             * Configures the boolean flag if the auto-id is used. Default is false.
             * @param isAutoId boolean flag to indicate if the auto-id is enabled
             * @return this builder
             */
            public CustomMilvusVectorStore.MilvusVectorStoreConfig.Builder withAutoId(boolean isAutoId) {
                this.isAutoId = isAutoId;
                return this;
            }

            /**
             * Configures the content field name. Default is {@value #CONTENT_FIELD_NAME}.
             * @param contentFieldName The name for the content field
             * @return this builder
             */
            public CustomMilvusVectorStore.MilvusVectorStoreConfig.Builder withContentFieldName(String contentFieldName) {
                this.contentFieldName = contentFieldName;
                return this;
            }

            /**
             * Configures the metadata field name. Default is
             * {@value #METADATA_FIELD_NAME}.
             * @param metadataFieldName The name for the metadata field
             * @return this builder
             */
            public CustomMilvusVectorStore.MilvusVectorStoreConfig.Builder withMetadataFieldName(String metadataFieldName) {
                this.metadataFieldName = metadataFieldName;
                return this;
            }

            /**
             * Configures the embedding field name. Default is
             * {@value #EMBEDDING_FIELD_NAME}.
             * @param embeddingFieldName The name for the embedding field
             * @return this builder
             */
            public CustomMilvusVectorStore.MilvusVectorStoreConfig.Builder withEmbeddingFieldName(String embeddingFieldName) {
                this.embeddingFieldName = embeddingFieldName;
                return this;
            }

            /**
             * {@return the immutable configuration}
             */
            public CustomMilvusVectorStore.MilvusVectorStoreConfig build() {
                return new CustomMilvusVectorStore.MilvusVectorStoreConfig(this);
            }

        }

    }
}
