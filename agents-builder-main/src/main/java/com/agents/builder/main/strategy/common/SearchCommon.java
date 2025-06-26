package com.agents.builder.main.strategy.common;

import com.agents.builder.main.service.IDatasetService;
import com.agents.builder.main.service.IModelService;
import com.agents.builder.main.service.IParagraphService;
import com.agents.builder.main.strategy.SearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public abstract class SearchCommon implements SearchStrategy {

    @Autowired
    @Lazy
    protected IDatasetService datasetService;
    @Autowired
    protected IModelService modelService;
    @Autowired
    protected IParagraphService paragraphService;
    @Autowired
    protected ThreadPoolTaskExecutor threadPoolTaskExecutor;
}
