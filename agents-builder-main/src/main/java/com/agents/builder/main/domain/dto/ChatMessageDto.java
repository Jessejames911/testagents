package com.agents.builder.main.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {

    @JsonProperty("form_data")
    private Map<String, Object> formData;

    private Map<String, Object> apiParams;

    private String message;

    @JsonProperty("re_chat")
    private Boolean reChat;

    private Long chatId;
}
