package com.agents.builder.main.node;

import com.agents.builder.common.core.workflow.chain.Chain;
import com.agents.builder.common.core.workflow.node.ChainNode;
import com.agents.builder.common.core.workflow.node.NodeResult;
import com.alibaba.fastjson2.JSON;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DirectReplyNode extends ChainNode {


    public DirectReplyNode(String id, String type, String name, Map<String, Object> nodeData, Map<String, Object> context, Map<String, Object> properties, Map<String, Object> config) {
        super(id, type, name, nodeData, context, properties, config);
    }

    @Override
    public NodeResult execute(Chain chain) {
        log.info("workflow==> 开始执行 DirectReply 节点 data: {}", nodeData);
        thisChain = chain;
        long startTime = System.currentTimeMillis();
        String replyType = (String) nodeData.get("reply_type");
        List<String> fields = (List<String>) nodeData.get("fields");
        String content = (String) nodeData.get("content");
        String result;
        if ("referencing".equals(replyType)){
            Map<String, Object> refField = getRefField(fields);
            Object resObj = refField.get(fields.get(1));
            if (resObj!=null && resObj instanceof String){
                result = (String)resObj;
            }else {
                result = JSON.toJSONString(resObj);
            }

        }else {
            result = setGolabalProperties(content);
        }
        return new NodeResult(Map.of("answer",result,
                "start_time",startTime),Map.of(), Flux.just(new ChatResponse(List.of(new Generation(new AssistantMessage(result))))),chain.isSimpleResult(this)?result:null);
    }



    @Override
    public void validate(Chain chain) {

    }

    @Override
    public Map<String, Object> getDetails(Integer index) {
        HashMap<String, Object> map = new HashMap<>() {
            {
                put("answer",context.get("answer"));
                put("index", index);
            }
        };
        map.putAll(commonDetails());
        return map;
    }
}
