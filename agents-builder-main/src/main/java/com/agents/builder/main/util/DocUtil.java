package com.agents.builder.main.util;

import java.util.Map;

public class DocUtil {
    public static String mapToStr(Map map) {
        StringBuilder str = new StringBuilder();
        map.forEach((k, v) -> str.append(k).append(": ").append(v).append(System.lineSeparator()));
        return str.toString();
    }
}
