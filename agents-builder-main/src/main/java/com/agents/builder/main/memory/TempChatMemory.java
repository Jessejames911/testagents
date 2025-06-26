package com.agents.builder.main.memory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.agents.builder.common.redis.utils.RedisUtils;
import com.agents.builder.common.redis.utils.RedissonUtils;
import com.agents.builder.main.constants.ChatConstants;
import com.agents.builder.main.domain.ApplicationChatRecord;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.agents.builder.main.constants.ChatConstants.*;
import static org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS;

@RequiredArgsConstructor
@Slf4j
@Component("tempChatMemory")
public class TempChatMemory implements ChatMemory {


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

        Long appId = (Long) userMsg.getMetadata().get(APP_ID);

        ApplicationChatRecord chatRecords = ApplicationChatRecord.builder()
                .id((Long) userMsg.getMetadata().get(CHAT_RECORD_ID))
                .problemText(question)
                .answerText(aiMessage.getText())
                .chatId(Long.parseLong(conversationId))
                .messageTokens((Long) aiMessage.getMetadata().get(MESSAGE_TOKENS))
                .applicationId(appId)
                .answerTokens((Long) aiMessage.getMetadata().get(ANSWER_TOKENS))
                .runTime(((Long)aiMessage.getMetadata().get(END_TIME)  - ((Long)userMsg.getMetadata().get(START_TIME)))/1000.0)
                .build();

        if (CollectionUtils.isEmpty(documents)){
            log.warn("未命中文档");
        }else {
            log.info("命中：{}篇文档",documents.size());
            chatRecords.setImproveParagraphIdList(documents.stream().map(ParagraphVo::getId).collect(Collectors.toList()));
        }
        saveToCache(chatRecords);
    }

    public void saveToCache(ApplicationChatRecord chatRecords) {
        String info = (String) RedisUtils.hGet(ChatConstants.TEMP_CHAT_RECORD_CACHE_KEY, chatRecords.getChatId().toString());
        List<ApplicationChatRecord> chatRecordList = JSON.parseArray(info, ApplicationChatRecord.class);
        RedisUtils.expire(TEMP_CHAT_APP_MAPPING_KEY+ chatRecords.getChatId().toString(),TEMP_CHAT_RECORD_EXPIRED_TIME);
        if (Objects.isNull(chatRecordList)){
            List<ApplicationChatRecord> cacheList = new ArrayList<>();
            cacheList.add(chatRecords);
            RedisUtils.hSet(TEMP_CHAT_RECORD_CACHE_KEY, chatRecords.getChatId().toString(),JSON.toJSONString(cacheList), ChatConstants.TEMP_CHAT_RECORD_EXPIRED_TIME);
            return;
        }
        chatRecordList.add(chatRecords);
        RedisUtils.hSet(TEMP_CHAT_RECORD_CACHE_KEY, chatRecords.getChatId().toString(),JSON.toJSONString(chatRecordList),ChatConstants.TEMP_CHAT_RECORD_EXPIRED_TIME);
    }


    @Override
    public List<Message> get(String conversationId, int lastN) {

        String info = (String) RedisUtils.hGet(ChatConstants.TEMP_CHAT_RECORD_CACHE_KEY, conversationId);
        List<ApplicationChatRecord> chatRecordsList = JSON.parseArray(info, ApplicationChatRecord.class);
        if (CollectionUtils.isEmpty(chatRecordsList)){
            return new ArrayList<>();
        }
        List<ApplicationChatRecord> recordsList = CollUtil.sub(chatRecordsList, 0, lastN);
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
        RedisUtils.hDel(TEMP_CHAT_RECORD_CACHE_KEY,conversationId);
        RedisUtils.del(TEMP_CHAT_APP_MAPPING_KEY+conversationId);
    }




    public static List<Message> toSpringAiMessage(ApplicationChatRecord chatRecords) {
        return List.of(new UserMessage(chatRecords.getProblemText()),
                new AssistantMessage(chatRecords.getAnswerText(), Map.of()));
    }


}
