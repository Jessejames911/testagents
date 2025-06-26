package com.agents.builder.common.core.workflow.node;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.agents.builder.common.core.workflow.chain.Chain;
import lombok.Data;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
public class NodeResult {

    private Map<String, Object> variable;

    private Map<String, Object> globalVariable;

    private Flux<ChatResponse> streamChatResponse;

    private String output;


    public NodeResult(Map<String, Object> variable, Map<String, Object> globalVariable, Flux<ChatResponse> streamChatResponse,String output) {
        this.variable = new HashMap<>(variable);
        this.globalVariable = new HashMap<>(globalVariable);
        this.streamChatResponse = streamChatResponse;
        this.output = output;
    }

    public Boolean isAssertion() {
        return variable.containsKey("branch_id");
    }

    public void writeContext(ChainNode currentNode, Chain chain) {
        if (CollUtil.isNotEmpty(variable)) {
            Long startTime =variable.get("start_time")==null?System.currentTimeMillis():(Long) variable.get("start_time");
            variable.put("run_time", (System.currentTimeMillis() - startTime)/1000.0);

            currentNode.getContext().putAll(variable);
            if (currentNode.isResult) {

                if (streamChatResponse != null && chain.getStream()){
                    chain.setStreamResponse(chain.getStreamResponse().concatWith(streamChatResponse));
                }
                if (StrUtil.isNotEmpty(output) &&!chain.getStream()){
                    chain.setOutput(chain.getOutput()+output);
                }

            }
        }
        if (CollUtil.isNotEmpty(globalVariable)) {
            chain.getGlobalData().putAll(globalVariable);
        }

    }
}
