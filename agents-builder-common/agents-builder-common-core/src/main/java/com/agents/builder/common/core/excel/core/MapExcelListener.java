package com.agents.builder.common.core.excel.core;

import cn.hutool.core.util.StrUtil;
import com.agents.builder.common.core.utils.JsonUtils;
import com.agents.builder.common.core.utils.StreamUtils;
import com.agents.builder.common.core.utils.ValidatorUtils;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.exception.ExcelDataConvertException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Excel 导入监听
 *
 * @author Yjoioooo
 *
 */
@Slf4j
@NoArgsConstructor
public class MapExcelListener extends AnalysisEventListener<Map<Integer, Object>> implements ExcelListener<Map<Integer, Object>> {

    /**
     * excel 表头数据
     */
    private Map<Integer, String> headMap;

    /**
     * 导入回执
     */
    private ExcelResult<Map<String, Object>> excelResult = new DefaultExcelResult<>();


    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headMap = headMap;
        log.debug("解析到一条表头数据: {}", JsonUtils.toJsonString(headMap));
    }

    @Override
    public void invoke(Map<Integer, Object> data, AnalysisContext context) {
        Map<String, Object> paramsMap = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            String s = headMap.get(i);
            Object o = data.get(i);
            //将表头作为map的key，每行每个单元格的数据作为map的value
            paramsMap.put(s, o);
        }

        excelResult.getList().add(paramsMap);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.debug("所有数据解析完成！");
    }


    @Override
    public ExcelResult<Map<Integer, Object>> getExcelResult() {
        return null;
    }

    public ExcelResult<Map<String, Object>> getResult() {
        return excelResult;
    }
}
