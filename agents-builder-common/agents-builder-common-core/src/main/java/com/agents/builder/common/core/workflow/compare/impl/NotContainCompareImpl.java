package com.agents.builder.common.core.workflow.compare.impl;

import cn.hutool.core.util.ObjectUtil;
import com.agents.builder.common.core.workflow.enums.CompareType;
import com.agents.builder.common.core.workflow.compare.Compare;
import org.springframework.stereotype.Service;

@Service
public class NotContainCompareImpl extends Compare {
    @Override
    public CompareType type() {
        return CompareType.NOT_CONTAINS;
    }

    @Override
     public Boolean compare(Object source, Object target) {
        return !ObjectUtil.contains(source, target);
    }
}
