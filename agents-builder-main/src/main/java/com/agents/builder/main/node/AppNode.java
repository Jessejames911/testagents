package com.agents.builder.main.node;

import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.utils.SpringUtils;
import com.agents.builder.common.core.workflow.DetailMessage;
import com.agents.builder.common.core.workflow.chain.Chain;
import com.agents.builder.common.core.workflow.node.ChainNode;
import com.agents.builder.common.core.workflow.node.NodeResult;
import com.agents.builder.main.domain.dto.ChatMessageDto;
import com.agents.builder.main.domain.vo.ApplicationVo;
import com.agents.builder.main.domain.vo.ChatMessageVo;
import com.agents.builder.main.enums.AppType;
import com.agents.builder.main.service.IApplicationChatService;
import com.agents.builder.main.service.IApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class AppNode extends ChainNode {

    private static final IApplicationService applicationService = SpringUtils.getBean(IApplicationService.class);

    private static final IApplicationChatService chatService = SpringUtils.getBean(IApplicationChatService.class);


    public AppNode(String id, String type, String name, Map<String, Object> nodeData, Map<String, Object> context, Map<String, Object> properties, Map<String, Object> config) {
        super(id, type, name, nodeData, context, properties, config);
    }

    @Override
    public NodeResult execute(Chain chain) {
        log.info("workflow==> 开始执行App节点 data: {}",nodeData);
        long startTime = System.currentTimeMillis();
        Long applicationId = (Long) nodeData.get("application_id");
        List<String> questionReferenceAddress = (List<String>) nodeData.get("question_reference_address");
        String question = (String) getRefField(questionReferenceAddress).get(questionReferenceAddress.get(1));
        ApplicationVo applicationVo = applicationService.queryById(applicationId);
        if (applicationVo == null){
            log.error("应用：{}不存在",applicationId);
            throw new ServiceException("应用不存在"+applicationId);
        }
        String chatId = chatService.clientOpen(applicationId);
        ChatMessageDto messageDto = ChatMessageDto.builder()
                .chatId(Long.parseLong(chatId))
                .message(question)
                .reChat(false).build();
        Flux<ChatMessageVo> streamChatMessageVo = null;
        String answer = null;
        if (chain.isStreamResult(this)){
            streamChatMessageVo = applicationService.streamChat(messageDto);
        }else {
            ChatMessageVo chatMessageVo = applicationService.chat(messageDto);
            answer = chatMessageVo.getContent();
        }

        return new NodeResult(Map.of(
                "question", question,
                "result", answer,
                "answer",answer,
                "start_time", startTime
        ), Map.of(), streamChatMessageVo.map(vo->new ChatResponse(List.of(new Generation(new AssistantMessage(vo.getContent()))))),chain.isSimpleResult(this)?answer:null);
    }

    @Override
    public void validate(Chain chain) {

    }

    @Override
    public Map<String, Object> getDetails(Integer index) {

        HashMap<String, Object> map = new HashMap<>() {
            {
                put("index", index);
                put("question", context.get("question"));
                put("answer", context.get("answer"));
                put("message_tokens", context.get("message_tokens"));
                put("info", nodeData);
                put("answer_tokens", context.get("answer_tokens"));
                put("total_tokens", context.get("total_tokens"));
            }
        };
        map.putAll(commonDetails());
        return map;
    }
}
