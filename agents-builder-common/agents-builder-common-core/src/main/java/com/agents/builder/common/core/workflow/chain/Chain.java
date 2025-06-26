package com.agents.builder.common.core.workflow.chain;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.agents.builder.common.core.utils.SpringUtils;
import com.agents.builder.common.core.workflow.LfEdge;
import com.agents.builder.common.core.workflow.enums.ChainNodeStatus;
import com.agents.builder.common.core.workflow.enums.ChainStatus;
import com.agents.builder.common.core.workflow.enums.NodeType;
import com.agents.builder.common.core.workflow.node.ChainNode;
import com.agents.builder.common.core.workflow.node.NodeResult;
import com.agents.builder.common.core.workflow.node.NodeResultFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class Chain {

    protected static final ThreadPoolTaskExecutor workFlowExecutor = SpringUtils.getBean("workFlowExecutor");

    protected final List<ChainNode> nodes;

    protected final List<LfEdge> edges;

    protected final List<ChainNode> nodeContext;

    protected final Map<String,Object> globalData;

    protected final Boolean stream = true;

    protected ChainStatus status = ChainStatus.READY;

    protected Flux<ChatResponse> streamResponse = Flux.just();

    protected String output;

    protected ChainNode currentNode;

    protected NodeResult currentResult;

    protected NodeResult lastResult;



    public Chain execute(ChainNode currentNode){
        if (currentNode == null){
            ChainNode startNode = getStartNode();
            currentNode = startNode;
        }
        NodeResultFuture nodeResultFuture = runNodeFuture(currentNode);
        NodeResult result = nodeResultFuture.getNodeResult();
        List<ChainNode> nodeList = getNextNodeList(currentNode, result);
        List<CompletableFuture<Void>> futureList = nodeList.stream().map(node -> CompletableFuture.runAsync(() -> execute(node), workFlowExecutor)).collect(Collectors.toList());
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()])).join();
        return this;
    }



    private NodeResultFuture runNodeFuture(ChainNode currentNode) {
        try {
            NodeResult result = currentNode.execute(this);
            result.writeContext(currentNode,this);
            currentNode.setNodeStatus(ChainNodeStatus.SUCCESS);
            nodeContext.add(currentNode);
            return new NodeResultFuture(result, null, 200);
        } catch (Exception e) {
            log.error("节点：{} 执行异常", currentNode.getName(),e);
            currentNode.setNodeStatus(ChainNodeStatus.ERROR);
            currentNode.setErrorMessage(ExceptionUtil.getMessage(e));
            nodeContext.add(currentNode);
            status = ChainStatus.ERROR;
            String msg = "节点："+ currentNode.getName()+" 执行发生异常";
            streamResponse = Flux.just(new ChatResponse(List.of(new Generation(new AssistantMessage(msg+ ExceptionUtil.stacktraceToOneLineString(e) + System.lineSeparator())))));
            return new NodeResultFuture(new NodeResult(Map.of(), Map.of(), Flux.just(new ChatResponse(List.of(new Generation(new AssistantMessage(msg))))), isSimpleResult(currentNode)?msg:null), e, 200);
        }
    }

    private ChainNode getNextNode() {
        if (currentNode==null){
            return getStartNode();
        }
        if (currentResult != null && currentResult.isAssertion()){
            for (LfEdge edge : this.edges) {
                if (edge.getSourceNodeId().equals(currentNode.getId())&&
                        (edge.getSourceNodeId() + "_" +(String) currentResult.getVariable().get("branch_id")+"_right").equals(edge.getSourceAnchorId())
                ){
                    return getNodeById(edge.getTargetNodeId());
                }
            }
        }else {
            for (LfEdge edge : this.edges) {
                if (edge.getSourceNodeId().equals(currentNode.getId())){
                    return getNodeById(edge.getTargetNodeId());
                }
            }
        }
        return null;
    }

    private List<ChainNode> getNextNodes(){
        List<LfEdge> edgeList = this.edges.stream().filter(edge -> edge.getSourceNodeId().equals(currentNode.getId())).collect(Collectors.toList());
        List<ChainNode> nodeList = new ArrayList<>();
        for (LfEdge edge : edgeList) {
            for (ChainNode node : this.nodes) {
                if (node.getId().equals(edge.getTargetNodeId())){
                    nodeList.add(node);
                }
            }
        }
        return nodeList;
    }

    private List<ChainNode> getNextNodeList(ChainNode currentNode,NodeResult currentResult){
        List<ChainNode> nodeList = new ArrayList<>();
        if (currentResult != null && currentResult.isAssertion()){
            for (LfEdge edge : this.edges) {
                if (edge.getSourceNodeId().equals(currentNode.getId())&&
                        (edge.getSourceNodeId() + "_" +(String) currentResult.getVariable().get("branch_id")+"_right").equals(edge.getSourceAnchorId())
                ){
                    if (dependentNodeBeenExecuted(edge.getTargetNodeId())) {
                        nodeList.add(getNodeById(edge.getTargetNodeId()));
                    }
                }
            }
        }else {
            for (LfEdge edge : this.edges) {
                if (edge.getSourceNodeId().equals(currentNode.getId()) && dependentNodeBeenExecuted(edge.getTargetNodeId())){
                    if (dependentNodeBeenExecuted(edge.getTargetNodeId())) {
                        nodeList.add(getNodeById(edge.getTargetNodeId()));
                    }
                }
            }
        }
        return nodeList;
    }

    private Boolean dependentNodeBeenExecuted(String nodeId){
        List<String> upNodeIdList = this.edges.stream().filter(edge -> edge.getTargetNodeId().equals(nodeId)).map(LfEdge::getSourceNodeId).collect(Collectors.toList());
        return upNodeIdList.stream().allMatch(nodeId1 -> nodeContext.stream().anyMatch(node -> node.getId().equals(nodeId1)));
    }

    public ChainNode getNodeById(String targetNodeId) {
        return CollectionUtil.findOne(nodes, node -> targetNodeId.equals(node.getId()));
    }

    public Boolean isEnd(ChainNode currentNode){
        for (LfEdge edge : this.edges) {
            if (edge.getSourceNodeId().equals(currentNode.getId())){
                return false;
            }
        }
        return true;
    }

    public Boolean hasNext(ChainNode currentNode,NodeResult currentResult) {
        if (currentNode == null ){
            if (getStartNode() != null)return true;
        }else {
            if (currentResult != null && currentResult.isAssertion()){
                for (LfEdge edge : this.edges) {
                    if (edge.getSourceNodeId().equals(currentNode.getId())&&
                            (edge.getSourceNodeId() + "_" +(String) currentResult.getVariable().get("branch_id")+"_right").equals(edge.getSourceAnchorId())
                    ){
                        return true;
                    }
                }
            }else {
                for (LfEdge edge : this.edges) {
                    if (edge.getSourceNodeId().equals(currentNode.getId())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected ChainNode getStartNode() {
        return CollectionUtil.findOne(nodes, node -> NodeType.START.getKey().equals(node.getType()));
    }

    public boolean isResult(ChainNode currentNode,NodeResult currentResult) {
        if (currentNode != null && currentNode.getNodeData() != null) {
            Map<String, Object> nodeParams = currentNode.getNodeData();
            return (Boolean) nodeParams.getOrDefault("is_result", !hasNext(currentNode,currentResult));
        }
        return false;
    }

    public List<ChainNode> getNodes() {
        return nodes;
    }

    public List<LfEdge> getEdges() {
        return edges;
    }

    public List<ChainNode> getNodeContext() {
        return nodeContext;
    }

    public Map<String, Object> getGlobalData() {
        return globalData;
    }

    public ChainStatus getStatus() {
        return status;
    }

    public Flux<ChatResponse> getStreamResponse() {
        return streamResponse;
    }

    public ChainNode getCurrentNode() {
        return currentNode;
    }

    public NodeResult getCurrentResult() {
        return currentResult;
    }

    public NodeResult getLastResult() {
        return lastResult;
    }


    public void setStreamResponse(Flux<ChatResponse> streamResponse) {
        this.streamResponse = streamResponse;
    }


    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Boolean getStream() {
        return stream;
    }

    public boolean isStreamResult(ChainNode node) {
        return (isEnd(node) || node.getIsResult()) && stream;
    }

    public boolean isSimpleResult(ChainNode node) {
        return (isEnd(node) || node.getIsResult()) && !stream;
    }
}
