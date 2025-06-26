package com.agents.builder.main.strategy.impl.chat;

import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.core.enums.StrStatus;
import com.agents.builder.main.advisor.ServiceChatMemoryAdvisor;
import com.agents.builder.main.constants.ChatConstants;
import com.agents.builder.main.domain.ChatMessage;
import com.agents.builder.main.domain.vo.ApplicationVo;
import com.agents.builder.main.domain.vo.ChatMessageVo;
import com.agents.builder.main.domain.vo.ModelVo;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.enums.AppType;
import com.agents.builder.main.memory.AiMessageChatMemory;
import com.agents.builder.main.strategy.common.ChatMessageCommon;
import com.agents.builder.main.tools.SystemTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS;

@Service
public class SimpleChatMessageStrategy extends ChatMessageCommon {
    @Override
    public Flux<ChatMessageVo> streamChat(ChatMessage chatMessage) {
        return doChat(chatMessage,true,Flux.class);
    }



    @Override
    public ChatMessageVo chat(ChatMessage chatMessage) {
        return doChat(chatMessage,false,ChatMessageVo.class);
    }


    @Override
    public AppType type() {
        return AppType.SIMPLE;
    }

    private <T> T doChat(ChatMessage chatMessage, boolean isStream,Class<T> clazz) {
        ApplicationVo app = chatMessage.getApp();

        if (app.getModelId() == null) {
            return buildReply("请先配置模型", chatMessage, true,isStream,clazz);
        }

        ModelVo modelVo = modelService.queryById(app.getModelId());

        if (modelVo == null || !StrStatus.SUCCESS.getKey().equals(modelVo.getStatus())) {
            return buildReply("模型不可用或不存在", chatMessage, true,isStream,clazz);
        }

        this.chatClient = ChatClient.builder(buildChatModel(modelVo))
                .defaultTools(new SystemTools()).build();


        if (app.getProblemOptimization()) {
            // 问题优化
            chatMessage.setOptimizedProblem(problemOptimize(chatMessage.getMessage(), app.getProblemOptimizationPrompt()));
        }
        List<ParagraphVo> similarityDocList = null;
        if (CollUtil.isNotEmpty(app.getDatasetIdList())) {
            similarityDocList = getSimilarityDoc(Optional.ofNullable(chatMessage.getOptimizedProblem()).orElse(chatMessage.getMessage()), app.getDatasetIdList(), app.getDatasetSetting());
            chatMessage.setDocumentList(similarityDocList);
        }

        List<ParagraphVo> finalSimilarityDocList = similarityDocList;
        ChatClient.ChatClientRequestSpec chatClientRequest = chatClient.prompt()
                .options(app.getModelParamsSetting().getChatOptions())
                .system(app.getModelSetting().getSystem())
                .advisors(advisorSpec -> {
                    advisorSpec.params(getBaseParams(chatMessage));
                    if (CollUtil.isNotEmpty(finalSimilarityDocList)) {
                        advisorSpec.params(Map.of(RETRIEVED_DOCUMENTS, finalSimilarityDocList));
                    }

                    advisorSpec.advisors(new ServiceChatMemoryAdvisor(chatMessage.getIsTempChat() ? tempChatMemory : chatMemory, chatMessage.getChatId().toString(), Optional.ofNullable(app.getDialogueNumber()).orElse(AiMessageChatMemory.DEFAULT_HIS_WINDOW_SIZE)));
                });

        if (CollUtil.isEmpty(app.getDatasetIdList()) || similarityDocList == null) {
            // 没有相关知识库文档
            if (ChatConstants.DESGINATED_ANSWER.equals(app.getDatasetSetting().getNoReferencesSetting().getStatus())) {
                // 指定回复
                return buildReply(app.getDatasetSetting().getNoReferencesSetting().getValue(), chatMessage, true,isStream,clazz);
            }
            // 继续向AI提问
            ChatClient.ChatClientRequestSpec chatRequest = chatClientRequest
                    .user(setPromptParams(app.getModelSetting().getNoReferencesPrompt(), chatMessage.getMessage(), ""));
            return isStream? buildReply(chatRequest.stream().content().map(answer -> buildChatMessageVo(answer, chatMessage, false)), chatMessage, true,clazz): buildReply(chatRequest.call().content(), chatMessage, true,isStream,clazz);
        }

        String data = similarityDocList.stream().map(ParagraphVo::getContent).collect(Collectors.joining(System.lineSeparator()));
        ChatClient.ChatClientRequestSpec chatRequest = chatClientRequest
                .user(setPromptParams(app.getModelSetting().getPrompt(), chatMessage.getMessage(), data));
        return isStream? buildReply(chatRequest.stream().content().map(answer -> buildChatMessageVo(answer, chatMessage, false)), chatMessage, true,clazz): buildReply(chatRequest.call().content(), chatMessage, true,isStream,clazz);

    }


}
