package com.agents.builder.common.core.workflow.compare.impl;

import cn.hutool.core.util.ObjUtil;
import com.agents.builder.common.core.workflow.enums.CompareType;
import com.agents.builder.common.core.workflow.compare.Compare;
import org.springframework.stereotype.Service;

@Service
public class IsNullCompareImpl extends Compare {
    @Override
    public CompareType type() {
        return CompareType.NULL;
    }

    @Override
     public Boolean compare(Object source, Object target) {
        return ObjUtil.isNull(source);
    }
}
