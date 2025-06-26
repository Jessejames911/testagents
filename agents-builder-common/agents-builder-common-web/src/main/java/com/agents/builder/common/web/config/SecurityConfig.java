package com.agents.builder.common.web.config;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;
import com.agents.builder.common.core.utils.SpringUtils;
import com.agents.builder.common.web.config.properties.SecurityProperties;
import com.agents.builder.common.web.handler.AllUrlHandler;
import com.agents.builder.common.web.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.*;

/**
 * 权限安全配置
 *
 *
 */

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final SecurityProperties securityProperties;

    /**
     * 注册sa-token的拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        AllUrlHandler allUrlHandler = SpringUtils.getBean(AllUrlHandler.class);

        // 注册路由拦截器，自定义验证规则
        registry.addInterceptor(new AuthInterceptor(handler -> {

            // 登录验证 -- 排除多个路径
            SaRouter
                // 获取所有的
                .match(allUrlHandler.getUrls())
                // 对未排除的路径进行检查
                .check(() -> {
                    // 检查是否登录 是否有token
                    StpUtil.checkLogin();

                });
        })).addPathPatterns("/**")
            // 排除不需要拦截的路径
            .excludePathPatterns(securityProperties.getExcludes());

    }







}
