package com.agents.builder.common.satoken.annotation;


import com.agents.builder.common.core.enums.OperateTargetType;
import com.agents.builder.common.core.enums.OperateType;

import java.lang.annotation.*;
import java.util.Collection;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE})
@Inherited
@Documented
public @interface CheckTargetOperate {

    String[] value();

    OperateType operateType() default OperateType.USE;

    OperateTargetType targetType() default OperateTargetType.APP;
}
