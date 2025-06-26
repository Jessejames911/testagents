package com.agents.builder.common.script.groovy;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


public class SpringGroovyFactory extends GroovyFactory {
    private static Logger logger = LoggerFactory.getLogger(SpringGroovyFactory.class);


    /**
     * inject action of spring
     * @param instance
     */
    @Override
    public void injectService(Object instance){
        if (instance==null) {
            return;
        }

        if (SpringUtil.getApplicationContext() == null) {
            return;
        }

        Field[] fields = instance.getClass().getDeclaredFields();


        for (Field field : ArrayUtil.addAll(fields, instance.getClass().getSuperclass().getDeclaredFields())) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            Object fieldBean = null;
            // with bean-id, bean could be found by both @Resource and @Autowired, or bean could only be found by @Autowired

            if (AnnotationUtils.getAnnotation(field, Resource.class) != null) {
                try {
                    Resource resource = AnnotationUtils.getAnnotation(field, Resource.class);
                    if (resource.name()!=null && resource.name().length()>0){
                        fieldBean = SpringUtil.getApplicationContext().getBean(resource.name());
                    } else {
                        fieldBean = SpringUtil.getApplicationContext().getBean(field.getName());
                    }
                } catch (Exception e) {
                }
                if (fieldBean==null ) {
                    fieldBean = SpringUtil.getApplicationContext().getBean(field.getType());
                }
            } else if (AnnotationUtils.getAnnotation(field, Autowired.class) != null) {
                Qualifier qualifier = AnnotationUtils.getAnnotation(field, Qualifier.class);
                if (qualifier!=null && qualifier.value()!=null && qualifier.value().length()>0) {
                    fieldBean = SpringUtil.getApplicationContext().getBean(qualifier.value());
                } else {
                    fieldBean = SpringUtil.getApplicationContext().getBean(field.getType());
                }
            }

            if (fieldBean!=null) {
                field.setAccessible(true);
                try {
                    field.set(instance, fieldBean);
                } catch (IllegalArgumentException e) {
                    logger.error(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

}
