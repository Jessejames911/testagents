package com.agents.builder.common.core.workflow.compare.impl;

import com.agents.builder.common.core.workflow.enums.CompareType;
import com.agents.builder.common.core.workflow.compare.Compare;
import org.springframework.stereotype.Service;

@Service
public class EqCompareImpl extends Compare {
    @Override
    public CompareType type() {
        return CompareType.EQUAL;
    }

    @Override
    public Boolean compare(Object source, Object target) {
        return source.equals(target);
    }
}
