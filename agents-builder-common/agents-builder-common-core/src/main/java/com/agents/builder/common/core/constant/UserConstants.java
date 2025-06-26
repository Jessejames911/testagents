package com.agents.builder.common.core.constant;

/**
 * 用户常量信息
 *
 *
 */
public interface UserConstants {

    /**
     * 正常状态
     */
    String NORMAL = "0";

    /**
     * 异常状态
     */
    String EXCEPTION = "1";

    /**
     * 用户正常状态
     */
    String USER_NORMAL = "0";

    /**
     * 用户封禁状态
     */
    String USER_DISABLE = "1";

    /**
     * 角色正常状态
     */
    String ROLE_NORMAL = "0";

    /**
     * 角色封禁状态
     */
    String ROLE_DISABLE = "1";

    /**
     * 是否为系统默认（是）
     */
    String YES = "Y";

    /**
     * 是否菜单外链（是）
     */
    String YES_FRAME = "0";

    /**
     * Layout组件标识
     */
    String LAYOUT = "Layout";

    /**
     * ParentView组件标识
     */
    String PARENT_VIEW = "ParentView";

    /**
     * InnerLink组件标识
     */
    String INNER_LINK = "InnerLink";

    /**
     * 用户名长度限制
     */
    int USERNAME_MIN_LENGTH = 2;
    int USERNAME_MAX_LENGTH = 20;

    /**
     * 密码长度限制
     */
    int PASSWORD_MIN_LENGTH = 5;
    int PASSWORD_MAX_LENGTH = 20;

    /**
     * 超级管理员ID
     */
    Long SUPER_ADMIN_ID = 1L;

    String COMMON_ADMIN = "admin";

    String COMMON_USER = "USER";

}
