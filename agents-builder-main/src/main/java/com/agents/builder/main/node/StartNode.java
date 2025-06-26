package com.agents.builder.main.node;


import com.agents.builder.common.core.workflow.chain.Chain;
import com.agents.builder.common.core.workflow.node.ChainNode;
import com.agents.builder.common.core.workflow.node.NodeResult;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class StartNode extends ChainNode {





    public StartNode(String id, String type, String name, Map<String, Object> nodeData, Map<String, Object> context, Map<String, Object> properties, Map<String, Object> config) {
        super(id, type, name, nodeData, context, properties, config);
    }

    @Override
    public NodeResult execute(Chain chain) {
        log.info("workflow==> 开始执行Start节点 data: {}",nodeData);
        return new NodeResult(Map.of("question",chain.getGlobalData().get("question"),
                "start_time",System.currentTimeMillis()),
                Map.of("start_time",System.currentTimeMillis()),null,null);
    }

    @Override
    public void validate(Chain chain) {

    }

    @Override
    public Map<String, Object> getDetails(Integer index) {
        HashMap<String, Object> map = new HashMap<>() {
            {
                put("question", context.get("question"));
                put("index", index);
            }
        };
        map.putAll(commonDetails());
        return map;
    }


}
