package com.agents.builder.common.satoken.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.agents.builder.common.core.domain.model.LoginUser;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.alibaba.ttl.TransmittableThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Slf4j
@Component
public class UserContextHolderInterceptor implements HandlerInterceptor {

    private final TransmittableThreadLocal<String> tokenTL =  new TransmittableThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        tokenTL.set(request.getHeader(StpUtil.getTokenName()));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        tokenTL.remove();
    }

    /**
     * 判断本次请求的数据类型是否为json
     *
     * @param request request
     * @return boolean
     */
    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType != null) {
            return StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE);
        }
        return false;
    }

    public LoginUser getLoginUser() {
        String token = tokenTL.get();
        return LoginHelper.getLoginUser(token);
    }

}
