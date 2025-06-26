package com.agents.builder.common.core.workflow.compare.impl;

import cn.hutool.core.util.ObjUtil;
import com.agents.builder.common.core.workflow.enums.CompareType;
import com.agents.builder.common.core.workflow.compare.Compare;
import com.agents.builder.common.core.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ContainCompareImpl extends Compare {
    @Override
    public CompareType type() {
        return CompareType.CONTAINS;
    }

    @Override
    public Boolean compare(Object source, Object target) {
        return ObjUtil.contains(source,target);

    }
}
