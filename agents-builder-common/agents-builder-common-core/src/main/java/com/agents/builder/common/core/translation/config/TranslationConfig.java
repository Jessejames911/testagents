package com.agents.builder.common.core.translation.config;

import com.agents.builder.common.core.translation.annotation.TranslationType;
import com.agents.builder.common.core.translation.core.handler.TranslationBeanSerializerModifier;
import com.agents.builder.common.core.translation.core.handler.TranslationHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import com.agents.builder.common.core.translation.core.TranslationInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 翻译模块配置类
 *
 * @author Yang Chao
 */
@Slf4j
@AutoConfiguration
public class TranslationConfig {

    @Autowired
    private List<TranslationInterface<?>> list;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        Map<String, TranslationInterface<?>> map = new HashMap<>(list.size());
        for (TranslationInterface<?> trans : list) {
            if (trans.getClass().isAnnotationPresent(TranslationType.class)) {
                TranslationType annotation = trans.getClass().getAnnotation(TranslationType.class);
                map.put(annotation.type(), trans);
            } else {
                log.warn(trans.getClass().getName() + " 翻译实现类未标注 TranslationType 注解!");
            }
        }
        TranslationHandler.TRANSLATION_MAPPER.putAll(map);
        // 设置 Bean 序列化修改器
        objectMapper.setSerializerFactory(
            objectMapper.getSerializerFactory()
                .withSerializerModifier(new TranslationBeanSerializerModifier()));
    }

}
