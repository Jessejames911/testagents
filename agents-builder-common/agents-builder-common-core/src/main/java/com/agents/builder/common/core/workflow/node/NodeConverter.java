package com.agents.builder.common.core.workflow.node;

import com.agents.builder.common.core.workflow.LfNode;

import java.util.List;

public interface NodeConverter {

    List<ChainNode> lfToChainNodes(List<LfNode> nodes);

}
