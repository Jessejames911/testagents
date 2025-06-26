package com.agents.builder.common.core.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.agents.builder.common.core.domain.dto.RoleDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * 登录用户身份权限
 *
 *
 */

@Data
@NoArgsConstructor
public class LoginUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @JsonProperty("user_id")
    private Long userId;


    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;


    private Set<String> permissions;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    @JsonProperty("nick_name")
    private String nickname;


    /**
     * 数据权限 当前角色ID
     */
    private String role;

    /**
     * 客户端
     */
    private String clientKey;

    /**
     * 设备类型
     */
    private String deviceType;

    private Long appId;

    private Long clientId;

    @JsonProperty("is_edit_password")
    private Boolean isEditPassword = false;

    /**
     * 获取登录id
     */
    public String getLoginId() {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return userId.toString();
    }

}
