package com.agents.builder.common.core.workflow.compare;

import com.agents.builder.common.core.workflow.enums.CompareType;

public abstract class Compare {

    public abstract CompareType type();


    public abstract Boolean compare(Object source, Object target);
}
