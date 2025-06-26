package com.agents.builder.common.web.handler;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.ReUtil;
import com.agents.builder.common.core.utils.SpringUtils;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.BooleanUtils.forEach;

/**
 * 获取所有Url配置
 */
@Data
@Component
public class AllUrlHandler implements InitializingBean {

    public static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

    private List<String> urls = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        Set<String> set = new HashSet<>();
        RequestMappingHandlerMapping mapping = SpringUtils.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        map.keySet().forEach(info -> {
            Set<PathPattern> pathPatterns = Objects.requireNonNull(info.getPathPatternsCondition().getPatterns());
            // 获取注解上边的 path 替代 path variable 为 *
            pathPatterns.forEach(url -> set.add(ReUtil.replaceAll(url.getPatternString(), PATTERN, "*")));

        });
        urls.addAll(set);
    }




}
