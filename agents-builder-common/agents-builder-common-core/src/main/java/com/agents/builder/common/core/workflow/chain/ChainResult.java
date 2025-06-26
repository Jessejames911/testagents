package com.agents.builder.common.core.workflow.chain;

import com.agents.builder.common.core.workflow.enums.ChainStatus;
import com.agents.builder.common.core.workflow.node.ChainNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ChainResult {

    protected List<ChainNode> nodeContext;

    protected Map<String,Object> globalData;

    protected ChainStatus status;

    protected Flux<ChatResponse> streamResponse;

    protected ChatResponse response;

    public List<Map<String,Object>> getNodeDetails() {
        if (CollectionUtils.isEmpty(nodeContext)){
            return Collections.emptyList();
        }
        List<Map<String,Object>> nodeDetails = new ArrayList<>();
        for (int i = 0; i < nodeContext.size(); i++) {
            nodeDetails.add(nodeContext.get(i).getDetails(i));
        }
        return nodeDetails;
    }
}
