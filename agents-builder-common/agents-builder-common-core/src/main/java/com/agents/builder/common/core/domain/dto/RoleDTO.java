package com.agents.builder.common.core.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 角色
 *
 *
 */

@Data
@NoArgsConstructor
public class RoleDTO implements Serializable {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限
     */
    private String roleKey;

    /**
     * 数据范围（1：所有数据权限；2：自定义数据权限；5：仅本人数据权限）
     */
    private String dataScope;

}
