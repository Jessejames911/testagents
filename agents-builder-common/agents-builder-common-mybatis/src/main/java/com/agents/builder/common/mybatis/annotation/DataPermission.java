package com.agents.builder.common.mybatis.annotation;

import com.agents.builder.common.core.enums.OperateTargetType;

import java.lang.annotation.*;

/**
 * 数据权限组
 *
 *
 * @version 3.5.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

    DataColumn[] value();

    OperateTargetType targetType();

}
