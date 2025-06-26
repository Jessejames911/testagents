package com.agents.builder.common.script.groovy.tools;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class HttpTool {

    public Object execute(Map<String, Object> params) {
        params.keySet().forEach(key->{
            if (params.get(key) instanceof String){
                params.put(key,StrUtil.isBlank((String) params.get(key)) ? null : (String) params.get(key));
            }
        });
        log.info("HttpTool execute ==== params: {}",params);
        String method = Optional.ofNullable((String)params.get("method")).orElse("get");
        String url = (String) params.get("url");
        if (!url.startsWith("http")){
            url = "http://"+url;
        }
        String headersStr = (String) params.get("headers");
        Map<String,String> headers = null;
        if (StrUtil.isNotBlank(headersStr)){
            headers = JSONObject.parseObject(headersStr, Map.class);
        }
        String paramsStr = (String) params.get("params");
        Map<String,Object> urlParams = null;
        if (StrUtil.isNotBlank(paramsStr)){
            urlParams = JSONObject.parseObject(paramsStr, Map.class);
        }
        Integer timeout = (Integer) params.get("timeout");


        HttpResponse resp = null;
        if ("get".equalsIgnoreCase(method)){
            resp = HttpRequest.get(url).form(urlParams)
                    .timeout(Optional.ofNullable(timeout).orElse(5000))
                    .headerMap(headers,true)
                    .execute();

        }else if ("post".equalsIgnoreCase(method)){
            resp = HttpRequest.post(url)
                    .form(urlParams)
                    .body((String) params.get("jsonbody"))
                    .timeout(Optional.ofNullable(timeout).orElse(5000))
                    .headerMap(headers,true)
                    .addHeaders(Map.of("Content-Type", "application/json"))
                    .execute();
        }else {
            log.info("不支持的请求方式");
        }
        if (resp==null || !resp.isOk()){
            log.error("请求失败");
            return null;
        }
        log.info("请求成功,响应结果:{}",resp.body());
        return JSONObject.parseObject(resp.body()).get("data");
    }
}
