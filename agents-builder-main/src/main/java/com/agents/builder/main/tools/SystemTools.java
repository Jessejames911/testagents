package com.agents.builder.main.tools;

import cn.hutool.core.date.DateUtil;
import org.springframework.ai.tool.annotation.Tool;

public class SystemTools {

    @Tool(description = "获取当前时间")
    public String getCurrentTime() {
        return DateUtil.now();
    }




}
