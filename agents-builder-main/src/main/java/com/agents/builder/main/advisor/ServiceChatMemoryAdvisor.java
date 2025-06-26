package com.agents.builder.main.advisor;

import com.agents.builder.main.constants.ChatConstants;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisorChain;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.agents.builder.main.constants.ChatConstants.*;

public class ServiceChatMemoryAdvisor extends MessageChatMemoryAdvisor {
    public ServiceChatMemoryAdvisor(ChatMemory chatMemory) {
        super(chatMemory);
    }

    public ServiceChatMemoryAdvisor(ChatMemory chatMemory, String defaultConversationId, int chatHistoryWindowSize) {
        super(chatMemory, defaultConversationId, chatHistoryWindowSize);
    }

    public ServiceChatMemoryAdvisor(ChatMemory chatMemory, String defaultConversationId, int chatHistoryWindowSize, int order) {
        super(chatMemory, defaultConversationId, chatHistoryWindowSize, order);
    }

    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        this.observeAfter(advisedResponse,System.currentTimeMillis());
        return advisedResponse;
    }

    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        Flux<AdvisedResponse> advisedResponses = this.doNextWithProtectFromBlockingBefore(advisedRequest, chain, this::before);
        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponses, advisedResponse -> observeAfter(advisedResponse,System.currentTimeMillis()));
    }

    private AdvisedRequest before(AdvisedRequest request) {
        String conversationId = this.doGetConversationId(request.adviseContext());
        int chatMemoryRetrieveSize = this.doGetChatMemoryRetrieveSize(request.adviseContext());
        List<Message> memoryMessages = ((ChatMemory)this.getChatMemoryStore()).get(conversationId, chatMemoryRetrieveSize);
        List<Message> advisedMessages = new ArrayList(request.messages());
        advisedMessages.addAll(memoryMessages);
        AdvisedRequest advisedRequest = AdvisedRequest.from(request).messages(advisedMessages).build();
        UserMessage userMessage = new UserMessage(request.userText(), request.media());
        Map<String, Object> metadata = userMessage.getMetadata();
        metadata.putAll(request.advisorParams());
        ((ChatMemory)this.getChatMemoryStore()).add(this.doGetConversationId(request.adviseContext()), userMessage);
        return advisedRequest;
    }

    private void observeAfter(AdvisedResponse advisedResponse,long endTime) {

        Usage usage = advisedResponse.response().getMetadata().getUsage();
        Long generationTokens = usage.getGenerationTokens();
        Long promptTokens = new Long(usage.getPromptTokens());
        Long totalTokens = new Long(usage.getTotalTokens());
        List<Message> assistantMessages = advisedResponse.response().getResults().stream().map((g) -> {
            Message message = g.getOutput();
            Map<String, Object> metadata = message.getMetadata();
            metadata.put(MESSAGE_TOKENS,promptTokens);
            metadata.put(ANSWER_TOKENS,generationTokens);
            metadata.put(TOTAL_TOKENS,totalTokens);
            metadata.put(END_TIME,endTime);
            return message;
        }).toList();
        this.getChatMemoryStore().add(this.doGetConversationId(advisedResponse.adviseContext()),  assistantMessages);
    }


    public static class Builder extends AbstractChatMemoryAdvisor.AbstractBuilder<ChatMemory> {
        protected Builder(ChatMemory chatMemory) {
            super(chatMemory);
        }

        public MessageChatMemoryAdvisor build() {
            return new MessageChatMemoryAdvisor((ChatMemory)this.chatMemory, this.conversationId, this.chatMemoryRetrieveSize, this.order);
        }
    }
}
