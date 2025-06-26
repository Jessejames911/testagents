package com.agents.builder.main.memory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.agents.builder.main.domain.ApplicationChatRecord;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.service.IApplicationChatRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.agents.builder.main.constants.ChatConstants.*;
import static org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS;

@Component("aiMessageChatMemory")
@RequiredArgsConstructor
@Slf4j
public class AiMessageChatMemory implements ChatMemory {

    private final IApplicationChatRecordService chatRecordsService;

    public static final Integer DEFAULT_HIS_WINDOW_SIZE = 1;

    private static Map<String, List<Message>> CONVERSATION_CACHE = new ConcurrentHashMap<>();

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (!CONVERSATION_CACHE.containsKey(conversationId)){
            CONVERSATION_CACHE.put(conversationId,new ArrayList<Message>());
        }
        for (Message message : messages) {

            if (MessageType.USER.getValue().equals(message.getMessageType().getValue())){
                CONVERSATION_CACHE.get(conversationId).add(message);
            }
            if (MessageType.ASSISTANT.getValue().equals(message.getMessageType().getValue())){
                Message userMsg = CollectionUtil.findOne(CONVERSATION_CACHE.get(conversationId), msg -> MessageType.USER.getValue().equals(msg.getMessageType().getValue()));
                CONVERSATION_CACHE.get(conversationId).clear();

                saveMessage(conversationId,message, userMsg);
            }
        }
    }

    private void saveMessage(String conversationId ,Message aiMessage, Message userMsg){
        log.info("持久化对话与文档关联");
        String question = (String) userMsg.getMetadata().get(QUESTION);

        // 添加文档关联
        List<ParagraphVo> documents = (List<ParagraphVo>) userMsg.getMetadata().get(RETRIEVED_DOCUMENTS);



        ApplicationChatRecord chatRecords = ApplicationChatRecord.builder()
                .id((Long) userMsg.getMetadata().get(CHAT_RECORD_ID))
                .problemText(question)
                .answerText(aiMessage.getText())
                .chatId(Long.parseLong(conversationId))
                .messageTokens((Long) aiMessage.getMetadata().get(MESSAGE_TOKENS))
                .answerTokens((Long) aiMessage.getMetadata().get(ANSWER_TOKENS))
                .runTime(((Long)aiMessage.getMetadata().get(END_TIME) - ((Long)userMsg.getMetadata().get(START_TIME)))/1000.0)
                .build();

        if (CollectionUtils.isEmpty(documents)){
            log.warn("未命中文档");
        }else {
            log.info("命中：{}篇文档",documents.size());
            chatRecords.setImproveParagraphIdList(documents.stream().map(ParagraphVo::getId).collect(Collectors.toList()));
        }
        chatRecordsService.insert(chatRecords);
    }


    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<ApplicationChatRecord> recordsList = CollUtil.reverse(chatRecordsService.getLastByChatId(Long.valueOf(conversationId), lastN));
        if (CollectionUtils.isEmpty(recordsList)){
            return new ArrayList<>();
        }

        List<Message> messages = new ArrayList<>();
        for (ApplicationChatRecord chatRecords : recordsList) {
            if (StringUtils.hasText(chatRecords.getProblemText())&& StringUtils.hasText(chatRecords.getAnswerText())) {
                messages.addAll(toSpringAiMessage(chatRecords));
            }
        }
        return messages;
    }

    @Override
    public void clear(String conversationId) {

    }




    public static List<Message> toSpringAiMessage(ApplicationChatRecord chatRecords) {
        return List.of(new UserMessage(chatRecords.getProblemText()),
                new AssistantMessage(chatRecords.getAnswerText(), Map.of()));
    }

}
