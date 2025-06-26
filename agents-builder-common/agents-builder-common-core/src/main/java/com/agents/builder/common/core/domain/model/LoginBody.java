package com.agents.builder.common.core.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录对象
 *
 *
 */

@Data
public class LoginBody implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;



    /**
     * 验证码
     */
    private String code;

    /**
     * 唯一标识
     */
    private String uuid;

}
