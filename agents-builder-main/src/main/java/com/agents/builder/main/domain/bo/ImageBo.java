package com.agents.builder.main.domain.bo;

import com.agents.builder.main.domain.Image;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 业务对象 image
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Image.class, reverseConvertGenerate = false)
public class ImageBo extends BaseEntity {

    /**
     * 
     */
    @NotNull(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long id;

    /**
     * 
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String image;

    /**
     * 
     */
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String imageName;


}
