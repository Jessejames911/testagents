package com.agents.builder.main.domain;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;
import java.util.Set;

/**
 * 对象 application_access_token
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "application_access_token",autoResultMap = true)
public class ApplicationAccessToken extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    private Long applicationId;

    /**
     *
     */
    private String accessToken;

    /**
     *
     */
    private Boolean isActive;

    /**
     *
     */
    private Long accessNum;

    /**
     *
     */
    private Boolean whiteActive;

    /**
     *
     */
    @TableField(value = "white_list", typeHandler = JacksonTypeHandler.class)
    private Set<String> whiteList;

    private Boolean showSource;

    private Boolean showHistory;


    private Boolean draggable;


    private Boolean showGuide;


    private String avatarUrl;


    private String floatIconUrl;


    private String userAvatarUrl;


    private Boolean disclaimer;


    private String disclaimerValue;

    @TableField(value = "custom_theme", typeHandler = JacksonTypeHandler.class)
    private CustomTheme customTheme = new  CustomTheme();

    @TableField(value = "float_location", typeHandler = JacksonTypeHandler.class)
    private FloatLocation floatLocation = new FloatLocation();

    @Data
    public static class CustomTheme {

        private String theme_color;


        private String header_font_color;
    }

    @Data
    public static class FloatLocation {
        private Y y = new Y();
        private X x = new X();


        @Data
        public static class Y {

            private String type = "bottom";

            private Integer value = 0;
        }

        @Data
        public static class X {
            private String type = "right";

            private Integer value = 0;

        }
    }


}
