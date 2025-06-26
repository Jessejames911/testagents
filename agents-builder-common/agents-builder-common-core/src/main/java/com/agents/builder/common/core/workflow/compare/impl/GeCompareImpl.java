package com.agents.builder.common.core.workflow.compare.impl;

import cn.hutool.core.util.ObjUtil;
import com.agents.builder.common.core.workflow.enums.CompareType;
import com.agents.builder.common.core.workflow.compare.Compare;
import com.agents.builder.common.core.exception.ServiceException;
import org.springframework.stereotype.Service;

@Service
public class GeCompareImpl extends Compare {
    @Override
    public CompareType type() {
        return CompareType.GREATER_THAN;
    }

    @Override
     public Boolean compare(Object source, Object target) {
        if (source instanceof Double && target instanceof Double){
            return ((Double) source) >= ((Double) target);
        }
        if (source instanceof Double && target instanceof String){
            return ((Double) source) >= Double.parseDouble((String) target);
        }
        if (source instanceof Integer && target instanceof Integer){
            return ((Integer) source) >= ((Integer) target);
        }
        if (source instanceof Integer && target instanceof String){
            return ((Integer) source) >= Integer.parseInt((String) target);
        }
        throw new ServiceException("类型错误");
    }
}
