package com.agents.builder.main.domain.vo;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.TeamMember;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.agents.builder.common.core.excel.annotation.ExcelDictFormat;
import com.agents.builder.common.core.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 视图对象 team_member
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = TeamMember.class)
public class TeamMemberVo  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String id;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long teamId;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long userId;

    private String username;

    private String email;

    private String type;


}
