package com.agents.builder.main.strategy.impl.chat;

import com.agents.builder.common.core.workflow.WorkflowExecutor;
import com.agents.builder.common.core.workflow.chain.ChainResult;
import com.agents.builder.main.domain.ApplicationChatRecord;
import com.agents.builder.main.domain.ChatMessage;
import com.agents.builder.main.domain.vo.ChatMessageVo;
import com.agents.builder.main.enums.AppType;
import com.agents.builder.main.service.IApplicationChatRecordService;
import com.agents.builder.main.strategy.common.ChatMessageCommon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * workflow chat message strategy
 *
 * @author Angus
 * @date 2024/11/07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowChatMessageStrategy extends ChatMessageCommon {

    private final WorkflowExecutor workflowExecutor;

    private final IApplicationChatRecordService chatRecordService;

    @Override
    public Flux<ChatMessageVo> streamChat(ChatMessage chatMessage) {
        return doChat(chatMessage, true, Flux.class);
    }


    @Override
    public ChatMessageVo chat(ChatMessage chatMessage) {
        return doChat(chatMessage, false, ChatMessageVo.class);
    }

    private <T> T doChat(ChatMessage chatMessage, boolean isStream,Class<T> clazz){
        ChainResult chainResult = null;
        try {
            chainResult = workflowExecutor.execute(new HashMap<>(Map.of("question", chatMessage.getMessage(),
                    "session_id", chatMessage.getChatId().toString(),
                    "isTempChat",chatMessage.getIsTempChat(),
                    "record_id", chatMessage.getRecordId(),
                    "stream",isStream)), chatMessage.getApp().getWorkFlow(),chatMessage.getUserParams(),chatMessage.getApiParams());
        } catch (Exception e) {
            log.error("工作流执行发生异常",e);
            return buildReply(e.getMessage(), chatMessage, true,isStream,clazz);
        }

        ChainResult finalChainResult = chainResult;


        return isStream ? buildReply(new MessageAggregator().aggregate(chainResult.getStreamResponse(), chatResponse -> {
            List<String> answer = chatResponse.getResults()
                    .stream()
                    .map(g -> g.getOutput().getText())
                    .toList();

            saveChatRecord(chatMessage, finalChainResult, answer.get(0));

        }).map(response -> buildChatMessageVo(response.getResult().getOutput().getText(),chatMessage,false)),chatMessage,false,clazz)
                : buildReply(handleResponseAfter(chatMessage,chainResult).getResult().getOutput().getText(),chatMessage,true,isStream,clazz);
    }

    private ChatResponse handleResponseAfter(ChatMessage chatMessage,ChainResult chainResult) {
        saveChatRecord(chatMessage, chainResult, chainResult.getResponse().getResult().getOutput().getText());
        return chainResult.getResponse();
    }

    private void saveChatRecord(ChatMessage chatMessage, ChainResult finalChainResult, String answer) {
        ApplicationChatRecord chatRecord = ApplicationChatRecord.builder()
                .id(chatMessage.getRecordId())
                .problemText(chatMessage.getMessage())
                .answerText(answer)
                .applicationId(chatMessage.getApp().getId())
                .details(finalChainResult.getNodeDetails())
                .chatId(chatMessage.getChatId())
                .build();
        // 保存对话记录
        if (chatMessage.getIsTempChat()) {
            tempChatMemory.saveToCache(chatRecord);
            return;
        }
        chatRecordService.insert(chatRecord);
    }




    @Override
    public AppType type() {
        return AppType.WORKFLOW;
    }


}
