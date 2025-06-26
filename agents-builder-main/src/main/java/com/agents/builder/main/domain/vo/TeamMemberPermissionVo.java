package com.agents.builder.main.domain.vo;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.TeamMemberPermission;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.agents.builder.common.core.excel.annotation.ExcelDictFormat;
import com.agents.builder.common.core.excel.convert.ExcelDictConvert;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 视图对象 team_member_permission
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = TeamMemberPermission.class)
public class TeamMemberPermissionVo  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String authTargetType;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long target;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Map<String, Boolean> operate = Map.of("USE",false,"MANAGE",false);

    private String operateStr;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("member_id")
    private String memberId;

    private String name;

    private String type;

    @JsonProperty("user_id")
    private Long userId;


}
