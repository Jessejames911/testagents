package com.agents.builder.common.core.service;

import java.util.Set;

/**
 * 通用 数据权限 服务
 *
 * @author Angus
 */
public interface DataScopeService {

    /**
     * 获取角色自定义权限
     *
     * @param roleId 角色id
     * @return 部门id组
     */
    String getUserCustom(Long userId,String targetType,boolean isSelect);

    Set<String> getAllPermissions(Long userId);

}
