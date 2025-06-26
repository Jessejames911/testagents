package com.agents.builder.common.mybatis.annotation;

import java.lang.annotation.*;

/**
 * 数据权限
 *
 * 一个注解只能对应一个模板
 *
 *
 * @version 3.5.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataColumn {

    /**
     * 占位符关键字
     */
    String[] key() default "target";

    /**
     * 占位符替换值
     */
    String[] value() default "id";



}
