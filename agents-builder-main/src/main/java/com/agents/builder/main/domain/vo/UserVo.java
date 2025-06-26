package com.agents.builder.main.domain.vo;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.User;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.agents.builder.common.core.excel.annotation.ExcelDictFormat;
import com.agents.builder.common.core.excel.convert.ExcelDictConvert;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 视图对象 user
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = User.class)
public class UserVo extends BaseEntity implements Serializable {

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
    private String email;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String phone;

    /**
     *
     */
    @ExcelProperty(value = "")
    @JsonProperty("nick_name")
    private String nickName;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String username;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String password;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String role;

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
    private String source;


}
