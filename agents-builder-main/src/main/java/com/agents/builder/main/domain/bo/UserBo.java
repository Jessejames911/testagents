package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.User;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 业务对象 user
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = User.class, reverseConvertGenerate = false)
public class UserBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @Email
    private String email;

    /**
     *
     */
    private String phone;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("nick_name")
    private String nickName;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String username;

    /**
     *
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String password;

    /**
     *
     */
    private String role;

    /**
     *
     *
     */
    @JsonProperty("is_active")
    private Boolean isActive;

    /**
     *
     */
    private String source;

    @JsonProperty("re_password")
    private String rePassword;

    @JsonProperty("email_or_username")
    private String email_or_username;

    private Integer code;

}
