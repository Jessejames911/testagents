package com.agents.builder.common.core.workflow;


import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.core.workflow.chain.Chain;
import com.agents.builder.common.core.workflow.chain.ChainResult;
import com.agents.builder.common.core.workflow.enums.NodeType;
import com.agents.builder.common.core.workflow.node.ChainNode;
import com.agents.builder.common.core.workflow.node.NodeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WorkflowExecutor {

    private final NodeConverter nodeConverter;

    public ChainResult execute(Map<String, Object> globalData, LogicFlow logicFlow, Map<String, Object> userParams, Map<String, Object> apiParams) {

        List<ChainNode> chainNodes = nodeConverter.lfToChainNodes(logicFlow.getNodes());

        Chain chain = new Chain(chainNodes, logicFlow.getEdges(), new ArrayList<ChainNode>(), buildGlobalData(globalData, userParams, apiParams)).execute(null);

        return new ChainResult(chain.getNodeContext(), chain.getGlobalData(), chain.getStatus(), chain.getStreamResponse(),new ChatResponse(List.of(new Generation(new AssistantMessage(chain.getOutput())))));
    }

    private Map<String, Object> buildGlobalData(Map<String, Object> globalData, Map<String, Object> userParams, Map<String, Object> apiParams) {
        if (CollUtil.isNotEmpty(userParams)) {
            globalData.putAll(userParams);
        }
        if (CollUtil.isNotEmpty(apiParams)) {
            globalData.putAll(apiParams);
        }
        return globalData;
    }


}
