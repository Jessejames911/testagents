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
import com.agents.builder.main.tools.SystemTools;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AiNode extends ChainNode {

    private static ChatModelBuilderContext chatModelBuilderContext =  SpringUtils.getBean(ChatModelBuilderContext.class);

    private static IModelService modelService  =  SpringUtils.getBean(IModelService.class);

    private final ChatClient chatClient;

    public AiNode(String id, String type, String name, Map<String, Object> nodeData, Map<String, Object> context, Map<String, Object> properties, Map<String, Object> config) {
        super(id, type, name, nodeData, context, properties, config);
        this.chatClient = buildChatClient(nodeData);
    }

    protected ChatClient buildChatClient(Map<String, Object> nodeData) {
        ModelVo modelVo = modelService.queryById(Long.parseLong((String) nodeData.get("model_id")));
        ChatModelBuilder chatModelBuilder = chatModelBuilderContext.getService(modelVo.getProvider(), () -> new StreamException("未找到匹配的模型构建器"));
        ChatModel chatModel = chatModelBuilder.build(modelVo.getCredential().getApiKey(), modelVo.getCredential().getApiBase(), modelVo.getModelName());

        Application.ModelParamsSetting modelParamsSetting = JSONObject.parseObject(JSON.toJSONString(nodeData.get("model_params_setting")),Application.ModelParamsSetting.class);

        return ChatClient.builder(chatModel)
                .defaultOptions(modelParamsSetting.getChatOptions())
                .defaultTools(new SystemTools()).build();
    }


    @Override
    public NodeResult execute(Chain chain) {
        long startTime = System.currentTimeMillis();

        String promptTemplate = (String) nodeData.get("prompt");
        String system = (String) nodeData.get("system");
        Integer dialogueNumber = (Integer) nodeData.get("dialogue_number");
        thisChain = chain;
        log.info("workflow==> 开始执行AI节点 data: {}",nodeData);

        // 获取历史会话

        // 构建提示词
        String prompt = setGolabalProperties(promptTemplate);
        log.info("workflow==> 构建后的提示词：{}", prompt);


        List<Message> historyMessage = getHistoryMessage(dialogueNumber);
        historyMessage.add(new SystemMessage(system));
        historyMessage.add(new UserMessage(prompt));

        ChatClient.ChatClientRequestSpec request = chatClient.prompt()
                .messages(historyMessage);

        String answer = "";

        Flux<ChatResponse> streamAnswer = null;

        final List<Long> promptTokenList = new ArrayList<>();

        final List<Long> generationTokenList = new ArrayList<>();

        final List<Long> totalTokenList = new ArrayList<>();

        if (chain.isStreamResult(this)) {
            streamAnswer = request.stream().chatResponse();
            streamAnswer.map(res->{
                Usage usage = res.getMetadata().getUsage();
                promptTokenList.add(new Long(usage.getPromptTokens()));
                generationTokenList.add(usage.getGenerationTokens());
                totalTokenList.add(new Long(usage.getTotalTokens()));
                return res;
            });
        } else {
            ChatResponse chatResponse = request.call().chatResponse();
            answer = chatResponse.getResult().getOutput().getText();
            promptTokenList.add(new Long(chatResponse.getMetadata().getUsage().getPromptTokens()));
            generationTokenList.add(chatResponse.getMetadata().getUsage().getGenerationTokens());
            totalTokenList.add(new Long(chatResponse.getMetadata().getUsage().getTotalTokens()));
        }


        return new NodeResult(Map.of("prompt", prompt,
                "message_tokens", promptTokenList.stream().mapToLong(item->item).sum(),
                "answer_tokens", generationTokenList.stream().mapToLong(item->item).sum(),
                "total_tokens", totalTokenList.stream().mapToLong(item->item).sum(),
                "question", prompt,
                "history_message", CollUtil.sub(historyMessage, 0, historyMessage.size()-2),
                "message_list", historyMessage,
                "result", answer,
                "answer",answer,
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
                put("index", index);
                put("question", context.get("question"));
                put("answer", context.get("answer"));
                put("message_tokens", context.get("message_tokens"));
                put("answer_tokens", context.get("answer_tokens"));
                put("total_tokens", context.get("total_tokens"));
                put("history_message", detailMessages);
            }
        };
        map.putAll(commonDetails());
        return map;
    }



}
