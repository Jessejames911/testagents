package com.agents.builder.main.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ChatMessageVo {

    private String content;

    @JsonProperty("chat_id")
    private Long chatId;

    @JsonProperty("is_end")
    private Boolean isEnd;

    private Long id;

    @JsonProperty("chat_record_id")
    private Long chatRecordId;

    private Boolean operate;

}
