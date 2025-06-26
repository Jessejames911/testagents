package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.ApplicationAccessToken;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * 业务对象 application_access_token
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ApplicationAccessToken.class, reverseConvertGenerate = false)
public class ApplicationAccessTokenBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "application不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("application")
    private Long applicationId;

    /**
     *
     */
    @NotBlank(message = "access_token不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("access_token")
    private String accessToken;

    /**
     *
     */
    @JsonProperty("is_active")
    private Boolean isActive;

    /**
     *
     */
    @NotNull(message = "access_num不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("access_num")
    private Long accessNum;

    /**
     *
     */
    @NotNull(message = "white_active不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("white_active")
    private Boolean whiteActive;

    /**
     *
     */
    @NotBlank(message = "white_list不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("white_list")
    private Set<String> whiteList;

    /**
     *
     */
    @NotNull(message = "show_source不能为空", groups = { AddGroup.class, EditGroup.class })
    @JsonProperty("show_source")
    private Boolean showSource;


    @JsonProperty("access_token_reset")
    private Boolean accessTokenReset = false;

    private Boolean show_history;

    private Boolean draggable;

    private Boolean show_guide;

    private Object avatar;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    private Object float_icon;

    @JsonProperty("float_icon_url")
    private String floatIconUrl;

    private Object user_avatar;

    @JsonProperty("user_avatar_url")
    private String userAvatarUrl;

    private Boolean disclaimer;

    private String disclaimer_value;

    private String custom_theme;

    private String float_location;
}
