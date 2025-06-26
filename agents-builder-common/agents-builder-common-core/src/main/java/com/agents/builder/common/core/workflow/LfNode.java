package com.agents.builder.common.core.workflow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;


/**
 * lf node
 *
 * @author Angus
 * @date 2024/08/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LfNode {
    private String id;
    private String type;
    private Integer x;
    private Integer y;
    private Map<String,Object> properties;
}
