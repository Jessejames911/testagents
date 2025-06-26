package com.agents.builder.main.node;


import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.utils.SpringUtils;
import com.agents.builder.common.core.workflow.chain.Chain;
import com.agents.builder.common.core.workflow.node.ChainNode;
import com.agents.builder.common.core.workflow.node.NodeResult;
import com.agents.builder.common.script.ScriptExecutor;
import com.agents.builder.common.script.context.ScriptExecutorContext;
import com.agents.builder.common.script.enums.ScriptLanguage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class FunctionNode extends ChainNode {


    private static ScriptExecutorContext scriptExecutorContext = SpringUtils.getBean(ScriptExecutorContext.class);


    public FunctionNode(String id, String type, String name, Map<String, Object> nodeData, Map<String, Object> context, Map<String, Object> properties, Map<String, Object> config) {
        super(id, type, name, nodeData, context, properties, config);
    }

    @Override
    @SneakyThrows
    public NodeResult execute(Chain chain) {
        log.info("workflow==> 开始执行 Function 节点 data: {}", nodeData);
        thisChain = chain;
        long startTime = System.currentTimeMillis();
        String code = (String) nodeData.get("code");
        String language = (String) nodeData.get("language");
        List<Map<String, Object>> fieldList = (List<Map<String, Object>>) nodeData.get("input_field_list");
        Map<String, Object> params = fieldList.stream()
                .collect(Collectors.toMap(k -> (String) k.get("name"), v -> convertValue((String) v.get("name"), v.get("value"), (String) v.get("type"), (Boolean) v.get("is_required"), (String) v.get("source")), (e1, e2) -> e2));

        context.put("params", params);

        ScriptExecutor scriptExecutor = scriptExecutorContext.getService(Optional.ofNullable(language).orElse(ScriptLanguage.JAVA.getKey()), () -> new ServiceException("暂不支持脚本语言" + language));

        Object resultObj = scriptExecutor.execute(code, params);


        Object answer = null;

        if (chain.isStreamResult(this)) {
            answer = resultObj;
        }

        // todo 结果解析

        return new NodeResult(Map.of("result", Optional.ofNullable(resultObj).orElse("null")
                , "start_time", startTime), Map.of(), answer != null ? Flux.just(new ChatResponse(List.of(new Generation(new AssistantMessage(answer.toString()))))) : null,chain.isSimpleResult(this)?resultObj.toString():null);
    }



    @Override
    public void validate(Chain chain) {

    }

    @Override
    public Map<String, Object> getDetails(Integer index) {
        HashMap<String, Object> map = new HashMap<>() {
            {
                put("params", context.get("params"));
                put("result", context.get("result"));
                put("index", index);
            }
        };
        map.putAll(commonDetails());
        return map;
    }
}
