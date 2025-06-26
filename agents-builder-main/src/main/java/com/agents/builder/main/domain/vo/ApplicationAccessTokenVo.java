package com.agents.builder.main.domain.vo;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.ApplicationAccessToken;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;


/**
 * 视图对象 application_access_token
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ApplicationAccessToken.class)
public class ApplicationAccessTokenVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("application")
    private Long applicationId;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("access_token")
    private String accessToken;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("is_active")
    private Boolean isActive;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("access_num")
    private Long accessNum;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("white_active")
    private Boolean whiteActive;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("white_list")
    private Set<String> whiteList;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("show_source")
    private Boolean showSource;

    @JsonProperty("show_history")
    private Boolean showHistory;

    @JsonProperty("draggable")
    private Boolean draggable;

    @JsonProperty("show_guide")
    private Boolean showGuide;


    private String avatar;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("user_avatar")
    private String userAvatar;

    @JsonProperty("float_icon")
    private String floatIcon;


    @JsonProperty("float_icon_url")
    private String floatIconUrl;


    @JsonProperty("user_avatar_url")
    private String userAvatarUrl;

    @JsonProperty("disclaimer")
    private Boolean disclaimer;

    @JsonProperty("disclaimer_value")
    private String disclaimerValue;

    @JsonProperty("custom_theme")
    private ApplicationAccessToken.CustomTheme customTheme;

    @JsonProperty("float_location")
    private ApplicationAccessToken.FloatLocation floatLocation;

}
