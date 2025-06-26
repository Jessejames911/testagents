package com.agents.builder.main.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.agents.builder.common.core.constant.Constants;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.utils.ServletUtils;
import com.agents.builder.common.web.handler.AuthHandler;
import com.agents.builder.main.domain.vo.ApplicationApiKeyVo;
import com.agents.builder.main.service.IApplicationApiKeyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class ApiKeyHandler implements AuthHandler {

    private final IApplicationApiKeyService apiKeyService;

    @Override
    public boolean handle(HttpServletResponse response) {
        String tokenValue = StpUtil.getStpLogic().getTokenValue(false);
        String secretKey = tokenValue.replace(Constants.APPLICATION + "-", "");
        if (StrUtil.isBlank(secretKey)){
            throw new ServiceException("token不合法");
        }
        ApplicationApiKeyVo apiKeyVo = apiKeyService.getBySecretKey(secretKey);
        if (apiKeyVo==null || !apiKeyVo.getIsActive()){
            throw new ServiceException("token不合法或被禁用");
        }
        if (apiKeyVo.getAllowCrossDomain() && CollUtil.isNotEmpty(apiKeyVo.getCrossDomainList())){
            // 设置跨域
            String domains = apiKeyVo.getCrossDomainList().stream().collect(Collectors.joining(","));
            response.setHeader("Access-Control-Allow-Origin", domains);
            response.setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }
        // 设置当前用户和权限

        return false;
    }

    @Override
    public boolean support(HttpServletRequest request) {
        return StpUtil.getStpLogic().getTokenValue(false).startsWith(Constants.APPLICATION);
    }
}
