package com.agents.builder.main.domain.vo;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.main.domain.Image;
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
 * 视图对象 image
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Image.class)
public class ImageVo extends BaseEntity implements Serializable {

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
    private String image;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String imageName;


}
