package com.agents.builder.main.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.core.constant.Constants;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.utils.ServletUtils;
import com.agents.builder.common.web.handler.AuthHandler;
import com.agents.builder.main.domain.ApplicationAccessToken;
import com.agents.builder.main.service.IApplicationAccessTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessTokenHandler implements AuthHandler {

    private final IApplicationAccessTokenService accessTokenService;

    @Override
    public boolean handle(HttpServletResponse response) {
        String tokenValue = StpUtil.getStpLogic().getTokenValue(false);
        String accessToken = (String) StpUtil.getStpLogic().getExtra(tokenValue, Constants.ACCESS_TOKEN);
        ApplicationAccessToken token = accessTokenService.getByToken(accessToken);
        if (token == null || !token.getIsActive()) {
            throw new ServiceException("accessToken不合法或被禁用",1002);
        }
        if (token.getWhiteActive() && CollUtil.isNotEmpty(token.getWhiteList())) {
            String clientIP = ServletUtils.getClientIP();
            if (!token.getWhiteList().contains(clientIP)) {
                throw new ServiceException("非法访问",1002);
            }
        }
        return true;
    }

    @Override
    public boolean support(HttpServletRequest request) {
        String tokenValue = StpUtil.getStpLogic().getTokenValue(false);
        Boolean isTempToken = null;
        try {
            isTempToken = (Boolean) StpUtil.getStpLogic().getExtra(tokenValue, Constants.TEMP_TOKEN);
        } catch (Exception e) {
            return false;
        }
        return isTempToken != null && isTempToken;
    }
}
