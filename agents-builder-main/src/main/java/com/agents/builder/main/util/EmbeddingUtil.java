package com.agents.builder.main.util;

import com.agents.builder.common.ai.vector.VectorStoreBuilder;
import com.agents.builder.common.core.utils.SpringUtils;
import com.agents.builder.main.domain.vo.DatasetVo;
import com.agents.builder.main.domain.vo.ModelVo;
import com.agents.builder.main.mapper.DatasetMapper;
import com.agents.builder.main.mapper.ModelMapper;
import org.springframework.ai.vectorstore.VectorStore;

public class EmbeddingUtil {

    private static VectorStoreBuilder vectorStoreBuilder = SpringUtils.getBean(VectorStoreBuilder.class);

    private static DatasetMapper datasetMapper = SpringUtils.getBean(DatasetMapper.class);

    private static ModelMapper modelMapper = SpringUtils.getBean(ModelMapper.class);
    public static VectorStore getVectorStoreByDatasetId(Long datasetId) {
        DatasetVo datasetVo = datasetMapper.selectVoById(datasetId);
        ModelVo modelVo = modelMapper.selectVoById(datasetVo.getEmbeddingModeId());
        return vectorStoreBuilder.build(modelVo.getProvider(), modelVo.getModelName(), modelVo.getCredential().getApiBase(), modelVo.getCredential().getApiKey(), "text_" + datasetId, null, false);
    }
}
