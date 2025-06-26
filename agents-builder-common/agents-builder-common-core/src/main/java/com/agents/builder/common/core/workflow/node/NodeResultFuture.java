package com.agents.builder.common.core.workflow.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NodeResultFuture {

    private NodeResult nodeResult;

    private Exception exception;

    private Integer status;

}
