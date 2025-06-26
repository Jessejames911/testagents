package com.agents.builder.main.strategy.impl.search;

import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.ai.enums.ModelType;
import com.agents.builder.common.ai.strategy.EmbeddingModelBuilder;
import com.agents.builder.common.ai.strategy.context.EmbeddingModelBuilderContext;
import com.agents.builder.common.ai.vector.VectorStoreBuilder;
import com.agents.builder.common.core.exception.StreamException;
import com.agents.builder.main.constants.DocMetaData;
import com.agents.builder.main.domain.dto.SearchDto;
import com.agents.builder.main.domain.vo.DatasetVo;
import com.agents.builder.main.domain.vo.ModelVo;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.enums.SearchMode;
import com.agents.builder.main.strategy.common.SearchCommon;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.agents.builder.common.ai.vector.CustomMilvusVectorStore.DISTANCE_FIELD_NAME;
import static com.agents.builder.main.constants.DocMetaData.METADATA_PARAGRAPH_ID;

import static org.springframework.ai.vectorstore.filter.Filter.ExpressionType.EQ;

@Service
@RequiredArgsConstructor
public class EmbeddingSearch extends SearchCommon {

    protected final VectorStoreBuilder vectorStoreBuilder;

    protected final EmbeddingModelBuilderContext embeddingModelBuilderContext;

    @Override
    public List<ParagraphVo> search(SearchDto dto) {

        List<DatasetVo> datasetVoList = datasetService.selectVoBatchIds(dto.getDatasetIdList());

        if (CollUtil.isEmpty(datasetVoList)){
            return Collections.emptyList();
        }

        List<Long> modelIdList = datasetVoList
                .stream().map(DatasetVo::getEmbeddingModeId)
                .collect(Collectors.toList());

        Map<Long, ModelVo> modelVoMap = modelService.getActivedByIdAndType(modelIdList, ModelType.EMBEDDING.getKey()).stream().collect(Collectors.toMap(ModelVo::getId, v -> v));

        List<CompletableFuture<List<ParagraphVo>>> futureList = new ArrayList<>();

        datasetVoList.forEach(datasetVo ->{
            ModelVo modelVo = modelVoMap.get(datasetVo.getEmbeddingModeId());
            VectorStore vectorStore = buildVectorStore(buildEmbeddingModel(modelVo), "text_" + datasetVo.getId(),null);

            futureList.add(CompletableFuture.supplyAsync(()->similaritySearch(dto, vectorStore), threadPoolTaskExecutor));
        });

        return futureList.stream().flatMap(future-> future.join().stream()).collect(Collectors.toList());
    }

    private List<ParagraphVo> similaritySearch(SearchDto dto, VectorStore vectorStore) {
        List<Document> documents = vectorStore.similaritySearch(buildSimilaritySearchRequest(dto));
        if (CollUtil.isEmpty(documents)){
            return Collections.emptyList();
        }
        Map<Long, Float> scoreMap = documents.stream()
                .collect(Collectors.toMap(K -> Long.valueOf((String) K.getMetadata().get(METADATA_PARAGRAPH_ID)), v -> (Float) v.getMetadata().get(DISTANCE_FIELD_NAME),(v1, v2) ->
                        v1 > v2 ? v1 : v2
                ));

        List<ParagraphVo> paragraphVoList = paragraphService.selectVoBatchIds(scoreMap.keySet());

        return paragraphVoList.stream().filter(paragraph->paragraph.getIsActive()).map(item -> {
            BigDecimal decimal = BigDecimal.valueOf((scoreMap.get(item.getId())));
            item.setSimilarity(decimal.setScale(2, RoundingMode.HALF_UP).doubleValue());
            return item;
        }).sorted(Comparator.comparing(ParagraphVo::getSimilarity).reversed()).collect(Collectors.toList());
    }


    private SearchRequest buildSimilaritySearchRequest(SearchDto dto) {
        return SearchRequest.builder()
                .query(dto.getQuery_text())
//                .withFilterExpression(buildExpression())
                .topK(dto.getTop_number())
                .similarityThreshold(dto.getSimilarity()).build();
    }

    private Filter.Expression buildExpression() {
        return new Filter.Expression(EQ, new Filter.Key(DocMetaData.METADATA_ACTIVE), new Filter.Value(true));
    }


    protected EmbeddingModel buildEmbeddingModel(ModelVo modelVo){
        EmbeddingModelBuilder builder = embeddingModelBuilderContext.getService(modelVo.getProvider(), () -> new StreamException("未找到匹配的模型构建器"));
        if (builder==null){
            throw new StreamException("未找到匹配的模型构建器");
        }
        return builder.build(modelVo.getCredential().getApiKey(),modelVo.getCredential().getApiBase(),modelVo.getModelName());
    }

    protected VectorStore buildVectorStore(EmbeddingModel embeddingModel, String collectionName, String databaseName){
        return vectorStoreBuilder.build(embeddingModel,collectionName,databaseName,false);
    }

    @Override
    public SearchMode mode() {
        return SearchMode.EMBEDDING;
    }
}
