package com.agents.builder.main.domain;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 对象 paragraph
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("paragraph")
public class Paragraph extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Long id;

    /**
     *
     */
    private String content;

    /**
     *
     */
    private String title;

    /**
     *
     */
    private String status;;

    /**
     *
     */
    private Long hitNum;

    /**
     *
     */
    private Boolean isActive;

    /**
     *
     */
    private Long datasetId;

    /**
     *
     */
    private Long documentId;


}
