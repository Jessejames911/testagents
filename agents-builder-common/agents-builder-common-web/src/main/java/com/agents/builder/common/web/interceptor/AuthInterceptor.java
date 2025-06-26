package com.agents.builder.common.web.interceptor;

import cn.dev33.satoken.fun.SaParamFunction;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.agents.builder.common.core.utils.SpringUtils;
import com.agents.builder.common.web.handler.AuthHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
public class AuthInterceptor extends SaInterceptor {

    private static final Collection<AuthHandler> authHandlerList = SpringUtils.getBeansOfType(AuthHandler.class).values();

    public AuthInterceptor() {
        super();
    }

    public AuthInterceptor(SaParamFunction<Object> auth) {
        super(auth);
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tokenValue = StpUtil.getStpLogic().getTokenValue(false);
        if (StrUtil.isBlank(tokenValue)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        AuthHandler authHandler = getAuthHandler(request);
        if (authHandler!=null){
            return authHandler.handle(response);
        }
        return super.preHandle(request,response, handler);
    }

    private AuthHandler getAuthHandler(HttpServletRequest request) {
        for (AuthHandler authHandler : authHandlerList) {
            if (authHandler.support(request)){
                return authHandler;
            }
        }
        return null;
    }



}
