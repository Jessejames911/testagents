package com.agents.builder.common.core.workflow.compare.impl;

import com.agents.builder.common.core.workflow.enums.CompareType;
import com.agents.builder.common.core.workflow.compare.Compare;
import com.agents.builder.common.core.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LenLtCompareImpl extends Compare {
    @Override
    public CompareType type() {
        return CompareType.LEN_LESS_THAN;
    }

    @Override
     public Boolean compare(Object source, Object target) {
        if (source instanceof Collection && target instanceof Integer){
            return ((Collection) source).size() < ((Integer) target);
        }
        if (source instanceof Collection && target instanceof String){
            return ((Collection) source).size() < Integer.parseInt((String) target);
        }
        if (source instanceof String && target instanceof Integer){
            return ((String) source).length() < ((Integer) target);
        }
        if (source instanceof String && target instanceof String){
            return ((String) source).length() < ((String) target).length();
        }
        throw new ServiceException("类型错误");
    }
}
