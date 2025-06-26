package com.agents.builder.main.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.agents.builder.common.core.config.AgentConfig;
import com.agents.builder.common.core.domain.R;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 首页
 *
 * @author Angus
 */
@SaIgnore
@RequiredArgsConstructor
@RestController
@RequestMapping("/profile")
public class ProfileController {

    /**
     * 系统基础配置
     */
    private final AgentConfig agentConfig;

    /**
     * 访问首页，提示语
     */
    @GetMapping
    @SaIgnore
    public R<?> index() {
        return R.ok(Map.of("IS_XPACK",false, "XPACK_LICENSE_IS_VALID",false, "version","v1.8.0"));
    }


}
