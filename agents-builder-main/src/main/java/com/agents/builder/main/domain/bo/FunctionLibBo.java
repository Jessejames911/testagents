package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.FunctionLib;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Map;

/**
 * 业务对象 function_lib
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = FunctionLib.class, reverseConvertGenerate = false)
public class FunctionLibBo extends BaseEntity {

    /**
     *
     */
    private Long id;

    /**
     *
     */
    @NotBlank(message = "name不能为空", groups = { AddGroup.class })
    private String name;

    /**
     *
     */
    private String description;

    /**
     *
     */
    @NotBlank(message = "code不能为空", groups = { AddGroup.class })
    private String code;

    /**
     *
     */
    @NotNull(message = "inputFieldList不能为空", groups = { AddGroup.class})
    @JsonProperty("input_field_list")
    private List<Map<String,Object>> inputFieldList;


    /**
     *
     */
    @JsonProperty("is_active")
    private Boolean isActive;

    /**
     *
     */
    @NotBlank(message = "permissionType不能为空", groups = { AddGroup.class })
    @JsonProperty("permission_type")
    private String permissionType;


}
