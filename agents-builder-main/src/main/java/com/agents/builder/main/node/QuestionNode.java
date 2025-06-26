package com.agents.builder.main.node;

import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.ai.strategy.ChatModelBuilder;
import com.agents.builder.common.ai.strategy.context.ChatModelBuilderContext;
import com.agents.builder.common.core.exception.StreamException;
import com.agents.builder.common.core.utils.SpringUtils;
import com.agents.builder.common.core.workflow.DetailMessage;
import com.agents.builder.common.core.workflow.chain.Chain;
import com.agents.builder.common.core.workflow.node.ChainNode;
import com.agents.builder.common.core.workflow.node.NodeResult;
import com.agents.builder.main.domain.Application;
import com.agents.builder.main.domain.vo.ModelVo;
import com.agents.builder.main.service.IModelService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class QuestionNode extends ChainNode {

    private static ChatModelBuilderContext chatModelBuilderContext = SpringUtils.getBean(ChatModelBuilderContext.class);

    private static IModelService modelService = SpringUtils.getBean(IModelService.class);

    private final ChatClient chatClient;

    public QuestionNode(String id, String type, String name, Map<String, Object> nodeData, Map<String, Object> context, Map<String, Object> properties, Map<String, Object> config) {
        super(id, type, name, nodeData, context, properties, config);
        this.chatClient = buildChatClient(nodeData);
    }

    @Override
    public NodeResult execute(Chain chain) {
        log.info("workflow==> 开始执行 Question 节点 data: {}", nodeData);
        thisChain = chain;

        long startTime = System.currentTimeMillis();

        Integer maxHisWindow = (Integer) nodeData.get("dialogue_number");

        String system = (String) nodeData.get("system");

        String promptTemplate = (String) nodeData.get("prompt");

        String prompt = setGolabalProperties(promptTemplate);


        List<Message> historyMessage = getHistoryMessage(maxHisWindow);
        historyMessage.add(new SystemMessage(system));
        historyMessage.add(new UserMessage(prompt));

        ChatClient.ChatClientRequestSpec request = chatClient.prompt()
                .messages(historyMessage);

        String answer = "";

        Flux<ChatResponse> streamAnswer = null;

        if (chain.isStreamResult(this)) {
            streamAnswer = request.stream().chatResponse();
        } else {
            answer = request.call().content();
            log.info("workflow==> 优化后的问题: {}", answer);
        }

        return new NodeResult(Map.of("system", system,
                "question", prompt,
                "prompt", prompt,
                "history_message", CollUtil.sub(historyMessage, 0, historyMessage.size() - 2),
                "message_list", historyMessage,
                "result", answer,
                "answer", answer,
                "start_time", startTime
        ), Map.of(), streamAnswer,chain.isSimpleResult(this)?answer:null);
    }

    @Override
    public void validate(Chain chain) {

    }

    @Override
    public Map<String, Object> getDetails(Integer index) {
        List<DetailMessage> detailMessages = convertDetailMessage((List<Message>) context.get("history_message"));
        HashMap<String, Object> map = new HashMap<>() {
            {
                put("system", nodeData.get("system"));
                put("question", context.get("question"));
                put("answer", context.get("answer"));
                put("message_tokens", context.get("message_tokens"));
                put("answer_tokens", context.get("answer_tokens"));
                put("history_message", detailMessages);
                put("index", index);
            }
        };
        map.putAll(commonDetails());
        return map;
    }

    protected ChatClient buildChatClient(Map<String, Object> nodeData) {
        ModelVo modelVo = modelService.queryById(Long.parseLong((String) nodeData.get("model_id")));
        ChatModelBuilder chatModelBuilder = chatModelBuilderContext.getService(modelVo.getProvider(), () -> new StreamException("未找到匹配的模型构建器"));
        ChatModel chatModel = chatModelBuilder.build(modelVo.getCredential().getApiKey(), modelVo.getCredential().getApiBase(), modelVo.getModelName());

        Application.ModelParamsSetting modelParamsSetting = JSONObject.parseObject(JSON.toJSONString(nodeData.get("model_params_setting")), Application.ModelParamsSetting.class);

        return ChatClient.builder(chatModel)
                .defaultOptions(modelParamsSetting.getChatOptions()).build();
    }

}
