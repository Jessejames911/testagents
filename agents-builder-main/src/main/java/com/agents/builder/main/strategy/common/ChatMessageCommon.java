package com.agents.builder.main.strategy.common;

import cn.hutool.core.util.StrUtil;
import com.agents.builder.common.ai.strategy.ChatModelBuilder;
import com.agents.builder.common.ai.strategy.context.ChatModelBuilderContext;
import com.agents.builder.common.core.exception.StreamException;
import com.agents.builder.main.domain.Application;
import com.agents.builder.main.domain.ChatMessage;
import com.agents.builder.main.domain.dto.SearchDto;
import com.agents.builder.main.domain.vo.ChatMessageVo;
import com.agents.builder.main.domain.vo.ModelVo;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.memory.AiMessageChatMemory;
import com.agents.builder.main.memory.TempChatMemory;
import com.agents.builder.main.service.IDatasetService;
import com.agents.builder.main.service.IModelService;
import com.agents.builder.main.strategy.ChatMessageStrategy;
import com.agents.builder.main.strategy.SearchStrategy;
import com.agents.builder.main.strategy.context.SearchContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.agents.builder.main.constants.ChatConstants.*;

@Slf4j
public abstract class ChatMessageCommon implements ChatMessageStrategy {

    @Autowired
    protected ChatModelBuilderContext chatModelBuilderContext;

    @Autowired
    protected IModelService modelService;

    @Autowired(required = false)
    protected ChatClient chatClient;

    @Autowired
    protected IDatasetService datasetService;

    @Autowired
    protected AiMessageChatMemory chatMemory;

    @Autowired
    protected TempChatMemory tempChatMemory;

    @Autowired
    protected SearchContext searchContext;


    protected String problemOptimize(String message, String prompt) {
        return chatClient.prompt().user(setPromptParams(prompt, message, "")).call().content();
    }

    protected List<ParagraphVo> getSimilarityDoc(String question, List<Long> datasetIdList, Application.DatasetSetting datasetSetting) {

        SearchStrategy searchStrategy = searchContext.getService(datasetSetting.getSearchMode(), () -> new StreamException("暂不支持此检索模式" + datasetSetting.getSearchMode()));

        return searchStrategy.search(SearchDto.builder()
                .similarity(datasetSetting.getSimilarity())
                .datasetIdList(datasetIdList)
                .query_text(question)
                .top_number(datasetSetting.getTopN())
                .maxParagraphCharNumber(datasetSetting.getMaxParagraphCharNumber()).build());
    }


    protected Map<String, Object> getBaseParams(ChatMessage chatMessage) {
        return Map.of(
                CHAT_RECORD_ID, chatMessage.getRecordId(),
                START_TIME, chatMessage.getStartTime(),
                APP_ID, chatMessage.getApp().getId(),
                QUESTION, chatMessage.getMessage(),
                OPTIMIZED_QUESTION, Optional.ofNullable(chatMessage.getOptimizedProblem()).orElse(chatMessage.getMessage()),
                MEMORY_HISTORY, Optional.ofNullable(chatMessage.getApp().getDialogueNumber()).orElse(0));
    }

    protected ChatModel buildChatModel(ModelVo modelVo) {
        ChatModelBuilder builder = chatModelBuilderContext.getService(modelVo.getProvider(), () -> new StreamException("未找到匹配的模型构建器"));
        if (builder == null) {
            throw new StreamException("未找到匹配的模型构建器");
        }
        return builder.build(modelVo.getCredential().getApiKey(), modelVo.getCredential().getApiBase(), modelVo.getModelName());
    }


    protected String setPromptParams(String prompt, String question, String data) {
        return prompt.replace("{question}", question).replace("{data}", data);
    }

    protected <T> T buildReply(String content, ChatMessage chatMessage, boolean isEnd, boolean isStream, Class<T> clazz) {
        return isStream?clazz.cast(Flux.just(buildChatMessageVo(content, chatMessage, isEnd))):clazz.cast(buildChatMessageVo(content, chatMessage, isEnd));
    }

    protected <T> T buildReply(Flux<ChatMessageVo> content, ChatMessage chatMessage, boolean isEnd,  Class<T> clazz) {
        return clazz.cast(content.concatWith(Flux.just(buildChatMessageVo("", chatMessage, isEnd))));
    }

    protected ChatMessageVo buildChatMessageVo(String content, ChatMessage chatMessage, boolean isEnd) {
        ChatMessageVo messageVo = ChatMessageVo.builder()
                .id(chatMessage.getRecordId())
                .chatRecordId(chatMessage.getRecordId())
                .chatId(chatMessage.getChatId())
                .content(content)
                .operate(true)
                .isEnd(isEnd)
                .build();
        if (isEnd && StrUtil.isNotEmpty(chatMessage.getContact())) {
            messageVo.setContent(Optional.ofNullable(content).orElse("") + System.lineSeparator() + "> " + chatMessage.getContact());
        }
        return messageVo;
    }
}
