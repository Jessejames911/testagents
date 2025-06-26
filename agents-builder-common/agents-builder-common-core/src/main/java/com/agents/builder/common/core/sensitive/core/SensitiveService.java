package com.agents.builder.common.core.sensitive.core;

/**
 * 脱敏服务
 * 默认管理员不过滤
 * 需自行根据业务重写实现
 *
 * @author Yang Chao
 * @version 3.6.0
 */
public interface SensitiveService {

    /**
     * 是否脱敏
     */
    boolean isSensitive(String roleKey, String perms);

}
