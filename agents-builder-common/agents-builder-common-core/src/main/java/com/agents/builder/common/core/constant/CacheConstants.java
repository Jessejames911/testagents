package com.agents.builder.common.core.constant;

/**
 * 缓存的key 常量
 *
 *
 */
public interface CacheConstants {

    /**
     * 在线用户 redis key
     */
    String ONLINE_TOKEN_KEY = "online_tokens:";

    /**
     * 参数管理 cache key
     */
    String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    String SYS_DICT_KEY = "sys_dict:";

    String AGENT_NODE_KEY = "agent_workflow:node:";

    String AGENT_CHAIN_KEY = "agent_workflow:chain";

    String AGENT_NODE_CMP_KEY = "agent_workflow:node_cmp";

    String CODE_EMAIL_PREFIX = "code_email:";
}
