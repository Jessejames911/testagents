package com.agents.builder.main.node;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.utils.SpringUtils;
import com.agents.builder.common.core.workflow.chain.Chain;
import com.agents.builder.common.core.workflow.node.ChainNode;
import com.agents.builder.common.core.workflow.node.NodeResult;
import com.agents.builder.main.domain.Application;
import com.agents.builder.main.domain.dto.SearchDto;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.strategy.SearchStrategy;
import com.agents.builder.main.strategy.context.SearchContext;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class DatasetSearchNode extends ChainNode {

    private static SearchContext searchContext = SpringUtils.getBean(SearchContext.class);

    public DatasetSearchNode(String id, String type, String name, Map<String, Object> nodeData, Map<String, Object> context, Map<String, Object> properties, Map<String, Object> config) {
        super(id, type, name, nodeData, context, properties, config);
    }

    @Override
    public NodeResult execute(Chain chain) {
        log.info("workflow==> 开始执行 KnowledgeSearch 节点 data: {}",nodeData);
        long startTime = System.currentTimeMillis();
        List<String> questionReference = (List<String>) nodeData.get("question_reference_address");
        if (CollUtil.isEmpty(questionReference) || questionReference.size()<2){
            log.error("知识库查询节点参数异常");
            throw new ServiceException("知识库查询节点参数异常");
        }
        thisChain = chain;
        String question = (String) getRefField(questionReference).get("question");
        Assert.notEmpty(question,"question is null");

        List<Long> datasetIdList = (List<Long>) nodeData.get("dataset_id_list");
        Application.DatasetSetting datasetSetting =JSON.parseObject(JSON.toJSONString(nodeData.get("dataset_setting")),Application.DatasetSetting.class);


        SearchStrategy searchStrategy = searchContext.getService(datasetSetting.getSearchMode(), () -> new ServiceException("暂不支持该检索模式"));

        List<ParagraphVo> paragraphVoList = searchStrategy.search(SearchDto.builder()
                .datasetIdList(datasetIdList)
                .query_text(question)
                .maxParagraphCharNumber(datasetSetting.getMaxParagraphCharNumber())
                .similarity(datasetSetting.getSimilarity())
                .top_number(datasetSetting.getTopN())
                .build());

        String documentText = paragraphVoList
                .stream().map(ParagraphVo::getContent)
                .collect(Collectors.joining(System.lineSeparator()));

        return new NodeResult(Map.of("paragraph_list",paragraphVoList,
                "question", question,
                "data",documentText,
                "start_time", startTime),Map.of(),null,null);
    }

    @Override
    public void validate(Chain chain) {

    }

    @Override
    public Map<String, Object> getDetails(Integer index) {
        HashMap<String, Object> map = new HashMap<>() {
            {
                put("question", context.get("question"));
                put("paragraph_list",context.get("paragraph_list"));
                put("index", index);
            }
        };
        map.putAll(commonDetails());
        return map;

    }
}
