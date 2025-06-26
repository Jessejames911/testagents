package com.agents.builder.common.core.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NodeType {

    BASE("base-node", "基础"),

    CONDITION("condition-node", "条件节点"),

    QUESTION("question-node", "问题节点"),

    REPLY("reply-node", "回复节点"),

    SEARCH_KNOWLEDGE("search-dataset-node", "知识库搜索节点"),

    FUNCTION("function-node", "自定义函数节点"),

    FUNCTION_LIB("function-lib-node", "函数库节点"),

    API("api-node", "api调用"),

    START("start-node", "开始节点"),

    AI_CHAT("ai-chat-node", "智能聊天节点"),

    APPLICATION("application-node", "应用节点"),
    ;

    private String key;

    private String name;


    public static NodeType getByKey(String key) {
        for (NodeType type : NodeType.values()) {
            if (type.getKey().equals(key)) {
                return type;
            }
        }
        return null;
    }

}
