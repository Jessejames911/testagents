package com.agents.builder.common.core.workflow.compare.impl;

import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.workflow.enums.CompareType;
import com.agents.builder.common.core.workflow.compare.Compare;

import org.springframework.stereotype.Service;

@Service
public class GtCompareImpl extends Compare {
    @Override
    public CompareType type() {
        return CompareType.GREATER_THAN;
    }

    @Override
     public Boolean compare(Object source, Object target) {
        if (source instanceof Double && target instanceof Double){
            return ((Double) source) > ((Double) target);
        }
        if (source instanceof Integer && target instanceof Integer){
            return ((Integer) source) > ((Integer) target);
        }
        throw new ServiceException("类型错误");
    }
}
