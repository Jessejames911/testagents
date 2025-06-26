package com.agents.builder.common.core.workflow.node;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.agents.builder.common.core.utils.SpringUtils;
import com.agents.builder.common.core.workflow.DetailMessage;
import com.agents.builder.common.core.workflow.chain.Chain;
import com.agents.builder.common.core.workflow.enums.ChainNodeStatus;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.util.*;
import java.util.stream.Collectors;

@Data
public abstract class ChainNode {

    protected final String id;

    protected final String type;

    protected final String name;

    protected ChainNodeStatus nodeStatus = ChainNodeStatus.READY;

    protected final Map<String, Object> nodeData;

    protected Map<String, Object> context;

    protected final Map<String, Object> config;

    protected final Map<String, Object> properties;

    protected String errorMessage;

    protected Boolean isResult;

    protected Chain thisChain;

    protected static ChatMemory chatMemory = SpringUtils.getBean("aiMessageChatMemory");

    protected static ChatMemory tempChatMemory = SpringUtils.getBean("tempChatMemory");


    public ChainNode(String id, String type, String name, Map<String, Object> nodeData, Map<String, Object> context, Map<String, Object> properties, Map<String, Object> config) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.nodeData = nodeData;
        this.context = context;
        this.properties = properties;
        this.config = config;
        if (nodeData != null) this.isResult = Optional.ofNullable((Boolean) nodeData.get("is_result")).orElse(false);
        else this.isResult = false;
    }

    public abstract NodeResult execute(Chain chain);


    public abstract void validate(Chain chain);


    public abstract Map<String, Object> getDetails(Integer index);


    public Map<String, Object> getField(List<String> fields) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String field : fields) {
            map.put(field, this.context.get(field));
        }
        return map;
    }


    protected Map<String, Object> getRefField(String nodeId, List<String> fields) {
        Map<String, Object> map = new HashMap<String, Object>();
        if ("global".equals(nodeId)) {
            for (String field : fields) {
                map.put(field, thisChain.getGlobalData().get(field));
            }
            return map;
        }
        return thisChain.getNodeById(nodeId).getField(fields);
    }

    protected Map<String, Object> getRefField(List<String> questionReference) {
        if (CollUtil.isEmpty(questionReference)) {
            return new HashMap<String, Object>();
        }
        return getRefField(CollUtil.sub(questionReference, 0, 1).get(0),
                CollUtil.sub(questionReference, 1, questionReference.size()));
    }

    protected Map<String, Object> commonDetails() {
        return new HashMap<>() {
            {
                put("name", properties.get("stepName"));
                put("run_time", context.get("run_time"));
                put("type", type);
                put("status", nodeStatus.getCode());
                put("err_message", errorMessage);
                put("global_fields", config.get("globalFields"));
            }
        };
    }

    protected Object convertValue(String fieldName, Object value, String type, Boolean isRequired, String source) {
        Object result = value;
        if (result==null)return null;
        if ("reference".equals(source)) {
            List<String> refList = (List<String>) value;
            if (CollUtil.isEmpty(refList)){
                return null;
            }
            // todo 类型校验
            result = getRefField(refList).get(refList.get(1));
        }
        if (StrUtil.isBlank(result.toString())) return null;
        switch (type) {
            case "string":
                result = result.toString();
                break;
            case "int":
                result = Integer.parseInt(result.toString());
                break;
            case "float":
                result = Double.parseDouble(result.toString());
                break;
            case "dict":
                if (result instanceof String)JSON.parseObject(result.toString(), Map.class);
                break;
            case "array":
                result = JSON.parseArray(result.toString(), List.class);
                break;
        }
        return result;
    }

    protected String setGolabalProperties(String text) {
        Map<Object, Object> context = new HashMap<>() {
            {
                put("global", thisChain.getGlobalData());
            }
        };
        for (ChainNode chainNode : thisChain.getNodeContext()) {
            Map<String, Object> config = (Map<String, Object>) chainNode.getProperties().get("config");
            if (CollUtil.isNotEmpty(config)) {
                List<Map<String, Object>> fields = (List<Map<String, Object>>) config.get("fields");
                if (CollUtil.isNotEmpty(fields)) {
                    for (Map<String, Object> field : fields) {
                        String globeLabel = chainNode.getProperties().get("stepName") + "." + field.get("value");
                        Object globeValue = chainNode.getContext().get(field.get("value"));
                        if (globeValue != null ) {
                            if (globeValue instanceof String){
                                text = text.replace(globeLabel, (String) globeValue);
                            }else {
                                text = text.replace(globeLabel, JSON.toJSONString(globeValue));
                            }

                        }
                    }
                }
                List<Map<String, Object>> globalFields = (List<Map<String, Object>>) config.get("globalFields");

                if (CollUtil.isNotEmpty(globalFields)) {
                    for (Map<String, Object> field : globalFields) {
                        String globeLabel = String.format("全局变量.%s", field.get("value"));
                        String globeValue = (String) ((Map<String, Object>) context.get("global")).get(field.get("value"));
                        if (globeValue != null) {
                            text = text.replace(globeLabel, globeValue);
                        }
                    }
                }
                context.put(chainNode.getId(), chainNode.getContext());

            }

        }
        return text.replace("{{", "").replace("}}", "");
    }

    protected List<Message> getHistoryMessage(Integer maxHisWindow) {
        if (maxHisWindow <= 0) {
            return new ArrayList<Message>();
        }
        String sessionId = (String) thisChain.getGlobalData().get("session_id");
        boolean isTempChat = (Boolean) thisChain.getGlobalData().get("isTempChat");
        return (isTempChat ? tempChatMemory : chatMemory).get(sessionId, maxHisWindow);
    }

    protected List<DetailMessage> convertDetailMessage(List<Message> messages) {
        if (CollUtil.isEmpty(messages)) return Collections.emptyList();
        return messages.stream().map(message -> new DetailMessage(message.getMessageType().getValue(), message.getText()))
                .collect(Collectors.toList());
    }


}
