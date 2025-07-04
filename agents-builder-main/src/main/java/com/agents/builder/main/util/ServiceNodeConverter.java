package com.agents.builder.main.util;

import com.agents.builder.common.core.workflow.LfNode;
import com.agents.builder.common.core.workflow.enums.NodeType;
import com.agents.builder.common.core.workflow.node.ChainNode;
import com.agents.builder.common.core.workflow.node.NodeConverter;
import com.agents.builder.main.node.*;
import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ServiceNodeConverter implements NodeConverter {

    public static Cache<String, ChainNode> NODE_CACHE = Caffeine.newBuilder()
            // 设置最后一次写入或访问后经过固定时间过期
            .expireAfterWrite(24, TimeUnit.HOURS)
            // 初始的缓存空间大小
            .initialCapacity(500)
            // 缓存的最大条数
            .maximumSize(10000)
            .build();

    @Override
    public List<ChainNode> lfToChainNodes(List<LfNode> nodes) {
        return nodes.stream()
                .map(node -> generateChainNode(node))
                .collect(Collectors.toList());
    }

    private static ChainNode generateChainNode(LfNode node) {
        String jsonNode = JSON.toJSONString(node);
        ChainNode chainNode = NODE_CACHE.getIfPresent(jsonNode);
        if (chainNode!=null){
            log.info("从缓存获取: {} 节点: {}信息", node.getType(), node.getId());
            chainNode.setContext(new HashMap<>());
            return chainNode;
        }
        String id = node.getId();
        String type = node.getType();
        Map<String, Object> properties = node.getProperties();
        String name = (String) properties.get("stepName");
        Map<String, Object> nodeData = (Map<String, Object>) properties.get("node_data");
        Map<String, Object> config = (Map<String, Object>) properties.get("config");
        NodeType nodeType = NodeType.getByKey(node.getType());
        if (nodeType == null) {
            throw new IllegalStateException("不支持的节点类型: " + type);
        }
        ChainNode newChainNode = null;

        switch (nodeType) {
            case BASE:
                newChainNode = new BaseNode(id, type, name, nodeData, new HashMap<>(), properties, config);
                break;
            case START:
                newChainNode =  new StartNode(id, type, name, nodeData, new HashMap<>(), properties, config);
                break;
            case AI_CHAT:
                newChainNode =  new AiNode(id, type, name, nodeData, new HashMap<>(), properties, config);
                break;
            case CONDITION:
                newChainNode =  new ConditionNode(id, type, name, nodeData, new HashMap<>(), properties, config);
                break;
            case QUESTION:
                newChainNode =  new QuestionNode(id, type, name, nodeData, new HashMap<>(), properties, config);
                break;
            case REPLY:
                newChainNode =  new DirectReplyNode(id, type, name, nodeData, new HashMap<>(), properties, config);
                break;
            case SEARCH_KNOWLEDGE:
                newChainNode =  new DatasetSearchNode(id, type, name, nodeData, new HashMap<>(), properties, config);
                break;
            case FUNCTION:
                newChainNode =  new FunctionNode(id, type, name, nodeData, new HashMap<>(), properties, config);
                break;
            case API:
                newChainNode =  new ApiNode(id, type, name, nodeData, new HashMap<>(), properties, config);
                break;
            case FUNCTION_LIB:
                newChainNode =  new FunctionLibNode(id, type, name, nodeData, new HashMap<>(), properties, config);
                break;
            case APPLICATION:
                newChainNode =  new AppNode(id, type, name, nodeData, new HashMap<>(), properties, config);
                break;
        }
        if (newChainNode==null) throw new IllegalStateException("不支持的节点类型: " + type);

        NODE_CACHE.put(jsonNode,newChainNode);
        return newChainNode;
    }
}
