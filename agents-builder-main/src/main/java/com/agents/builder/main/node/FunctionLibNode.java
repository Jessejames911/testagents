package com.agents.builder.main.node;


import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.utils.SpringUtils;
import com.agents.builder.common.core.workflow.chain.Chain;
import com.agents.builder.common.core.workflow.node.ChainNode;
import com.agents.builder.common.core.workflow.node.NodeResult;
import com.agents.builder.common.script.ScriptExecutor;
import com.agents.builder.common.script.context.ScriptExecutorContext;
import com.agents.builder.common.script.enums.ScriptLanguage;
import com.agents.builder.main.domain.vo.FunctionLibVo;
import com.agents.builder.main.service.IFunctionLibService;
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
public class FunctionLibNode extends ChainNode {

    private static ScriptExecutorContext scriptExecutorContext = SpringUtils.getBean(ScriptExecutorContext.class);

    private static IFunctionLibService functionLibService = SpringUtils.getBean(IFunctionLibService.class);

    public FunctionLibNode(String id, String type, String name, Map<String, Object> nodeData, Map<String, Object> context, Map<String, Object> properties, Map<String, Object> config) {
        super(id, type, name, nodeData, context, properties, config);
    }

    @Override
    @SneakyThrows
    public NodeResult execute(Chain chain) {
        log.info("workflow==> 开始执行 Function-Lib 节点 data: {}", nodeData);
        thisChain = chain;
        long startTime = System.currentTimeMillis();
        Long functionLibId = Long.parseLong(nodeData.get("function_lib_id").toString());
        FunctionLibVo functionLibVo = functionLibService.queryById(functionLibId);

        List<Map<String, Object>> fieldList = (List<Map<String, Object>>) nodeData.get("input_field_list");

        Map<String, Object> parameters = fieldList.stream()
                .collect(HashMap::new, (map, item) -> map.put((String) item.get("name"), convertValue((String) item.get("name"), item.get("value"), (String) item.get("type"), (Boolean) item.get("is_required"), (String) item.get("source"))),
                        HashMap::putAll);

        context.put("params", parameters);

        ScriptExecutor scriptExecutor = scriptExecutorContext.getService(Optional.ofNullable(functionLibVo.getLanguage()).orElse(ScriptLanguage.JAVA.getKey()), () -> new ServiceException("暂不支持脚本语言" + functionLibVo.getLanguage()));

        Object resultObj = scriptExecutor.execute(functionLibVo.getCode(), parameters);


        Object answer = null;

        if (chain.isStreamResult(this)) {
            answer = resultObj;
        }

        return new NodeResult(Map.of("result", Optional.ofNullable(resultObj).orElse("无结果")
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
