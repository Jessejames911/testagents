package com.agents.builder.main.node;

import cn.hutool.http.HttpRequest;
import com.agents.builder.common.core.domain.R;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.core.workflow.chain.Chain;
import com.agents.builder.common.core.workflow.node.ChainNode;
import com.agents.builder.common.core.workflow.node.NodeResult;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
public class ApiNode extends ChainNode {


    private static final int DEFAULT_TIMEOUT = 10000;

    public ApiNode(String id, String type, String name, Map<String, Object> nodeData, Map<String, Object> context, Map<String, Object> properties, Map<String, Object> config) {
        super(id, type, name, nodeData, context, properties, config);
    }

    @Override
    public NodeResult execute(Chain chain) {
        log.info("workflow==> 开始执行 Api 节点 data: {}",nodeData);
        thisChain = chain;
        long startTime = System.currentTimeMillis();
        String method = (String) nodeData.get("method");
        String url = (String) nodeData.get("url");
        Integer timeout = (Integer) nodeData.get("timeout");

        Map<String, Object> variables = (Map<String, Object>) nodeData.get("tableData");

        Map<String, Object> params = handleParams(variables.get("params"));
        Map<String, String> headers = handleHeaders(nodeData.get("headers"));


        String resultStr = null;
        log.info("请求参数：{}",JSON.toJSONString(params));
        switch (method){
            case "GET":
                resultStr = HttpRequest.get(url)
                        .form(params)
                        .timeout(Optional.ofNullable(timeout).orElse(DEFAULT_TIMEOUT))
                        .addHeaders(headers)
                        .execute()
                        .body();
                break;
            case "POST":
                resultStr = HttpRequest.post(url)
                        .body(JSON.toJSONString(params))
                        .timeout(Optional.ofNullable(timeout).orElse(DEFAULT_TIMEOUT))
                        .addHeaders(headers)
                        .execute()
                        .body();
                break;
            default:
                throw new RuntimeException("不支持的请求方式");
        }

        if (StringUtils.isEmpty(resultStr)){
            throw new ServiceException("请求返回结果为空");
        }
        log.info("请求返回结果：{}",resultStr);
        R result = JSON.parseObject(resultStr, R.class);
        if (result.getCode()!=R.SUCCESS && result.getCode()!=0){
            throw new ServiceException(result.getMessage());
        }


        return new NodeResult(Map.of("result",result.getData(),
                "data",result.getData(),
                "start_time", startTime,
                "headers",headers,
                "params",params,
                "method",method,
                "url",url),Map.of(), null,null);
    }

    private Map<String, String> handleHeaders(Object headers) {
        Map<String, Object> headerMap = (Map<String, Object>) headers;
        if (CollectionUtils.isEmpty(headerMap)){
            return null;
        }
        Map<String, String> resMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof List<?>){
                List<String> valueList = (List<String>) value;
                String field = (String) getRefField(valueList).get(valueList.get(1));
                entry.setValue(field);
                resMap.put(entry.getKey(), field);
            }
            if (value instanceof String){
                resMap.put(entry.getKey(), (String) value);
            }
        }

        return resMap;
    }

    private Map<String, Object> handleParams(Object params) {
        Map<String, Object> resMap = new HashMap<>();

        List<Map<String, Object>> paramsMapList = (List<Map<String, Object>>) params;
        if (CollectionUtils.isEmpty(paramsMapList)){
            return null;
        }

        for (Map<String, Object> paramsMap : paramsMapList) {
            String paramType = (String) paramsMap.get("type");
            String paramName = (String) paramsMap.get("name");
            String source = (String) paramsMap.get("source");
            Object paramValue  = paramsMap.get("value");

            if (StringUtils.isEmpty(paramName)){
                continue;
            }
            Object value = null;
            if ("custom".equals(source)){
                value = buildCustomParam(paramType, paramValue);
            }else {
                value = buildRefParam(paramType, paramValue);
            }
            resMap.put(paramName, value);
        }
        return resMap;
    }

    private Object buildRefParam(String paramType, Object paramValue) {
        List<String> valueList = (List<String>) paramValue;
        return getRefField(valueList).get(valueList.get(1));
    }

    private Object buildCustomParam(String paramType, Object paramValue) {
        String field = (String) paramValue;
        switch(paramType) {
            case "array":
                return Arrays.asList(field.split(","));
            case "dict":
                return JSON.parseObject(field);
            default:
                return field;
        }
    }


    @Override
    public void validate(Chain chain) {

    }

    @Override
    public Map<String, Object> getDetails(Integer index) {
        HashMap<String, Object> map = new HashMap<>() {
            {
                put("index", index);
                put("params", context.get("params"));
                put("result",context.get("result"));
                put("headers",context.get("headers"));
                put("method",context.get("method"));
                put("url",context.get("url"));
            }
        };
        map.putAll(commonDetails());
        return map;
    }
}
