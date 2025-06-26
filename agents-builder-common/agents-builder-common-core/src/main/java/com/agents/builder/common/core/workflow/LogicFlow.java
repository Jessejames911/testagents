package com.agents.builder.common.core.workflow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * logic flow
 *
 * @author Angus
 * @date 2024/11/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogicFlow {
    List<LfNode> nodes;

    List<LfEdge> edges;


}
