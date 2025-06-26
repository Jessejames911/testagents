package com.agents.builder.main.node;

import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.utils.SpringUtils;
import com.agents.builder.common.core.workflow.chain.Chain;
import com.agents.builder.common.core.workflow.compare.CompareContext;
import com.agents.builder.common.core.workflow.node.ChainNode;
import com.agents.builder.common.core.workflow.node.NodeResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ConditionNode extends ChainNode {

    private static CompareContext compareContext = SpringUtils.getBean(CompareContext.class);

    public ConditionNode(String id, String type, String name, Map<String, Object> nodeData, Map<String, Object> context, Map<String, Object> properties, Map<String, Object> config) {
        super(id, type, name, nodeData, context, properties, config);
    }


    @Override
    public NodeResult execute(Chain chain) {
        long startTime = System.currentTimeMillis();
        thisChain = chain;
        log.info("workflow==> 开始执行 Condition 节点 data: {}", nodeData);

        List<Map<String, Object>> branchList = (List<Map<String, Object>>) nodeData.get("branch");

        Map<String, Object> branch = getBranch(branchList);

        return new NodeResult(Map.of("branch_id", branch.get("id"),
                "branch_name", branch.get("type"),
                "start_time", startTime), Map.of(), null,null);
    }

    @Override
    public void validate(Chain chain) {

    }

    private Map<String, Object> getBranch(List<Map<String, Object>> branchList) {
        for (Map<String, Object> branch : branchList) {
            if (branchAssertion(branch)) {
                return branch;
            }
        }
        return null;
    }


    private Boolean branchAssertion(Map<String, Object> branch) {
        if ("ELSE".equals(branch.get("type"))){
            return true;
        }
        List<Boolean> conditionList = ((List<Map<String, Object>>) branch.get("conditions"))
                .stream().map(item -> assertion((List<String>) item.get("field"), (String) item.get("compare"), item.get("value")))
                .collect(Collectors.toList());

        String condition = (String) branch.get("condition");
        if ("and".equals(condition)) {
            return BooleanUtils.and(conditionList.toArray(new Boolean[conditionList.size()]));
        }
        return BooleanUtils.or(conditionList.toArray(new Boolean[conditionList.size()]));
    }

    private Boolean assertion(List<String> fieldList, String compare, Object value) {
        Map<String, Object> refField = getRefField(fieldList);
        return compareContext
                .getService(compare, () -> new ServiceException("不支持的比较类型：" + compare))
                .compare(refField.get(fieldList.get(1)), value);
    }


    @Override
    public Map<String, Object> getDetails(Integer index) {
        HashMap<String, Object> map = new HashMap<>() {
            {
                put("index", index);
                put("branch_id", context.get("branch_id"));
                put("branch_name", context.get("branch_name"));
            }
        };
        map.putAll(commonDetails());
        return map;
    }
}
