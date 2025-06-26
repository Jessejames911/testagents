package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.Model;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 业务对象 model
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Model.class, reverseConvertGenerate = false)
public class ModelBo extends BaseEntity {

    /**
     *
     */
    private Long id;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String model_type;

    /**
     *
     */
    @JsonProperty("model_name")
    private String modelName;

    /**
     *
     */
    private String provider;

    /**
     *
     */
    private Model.Credential credential;


    /**
     *
     */
    private Map<String,Object> meta;

    /**
     *
     */
    private String status;

    /**
     *
     */
    @JsonProperty("permission_type")
    private String permissionType;


}
