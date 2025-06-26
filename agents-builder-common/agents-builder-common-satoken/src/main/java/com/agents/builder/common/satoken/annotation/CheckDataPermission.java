package com.agents.builder.common.satoken.annotation;


import cn.dev33.satoken.annotation.SaMode;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE})
@Inherited
@Documented
public @interface CheckDataPermission {

    /**
     * 需要校验的权限码 [ 数组 ]
     *
     * @return /
     */
    String value() default "";


    SaMode mode() default SaMode.AND;
}
