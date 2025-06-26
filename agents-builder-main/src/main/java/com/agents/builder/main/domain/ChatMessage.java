package com.agents.builder.main.domain;

import com.agents.builder.main.domain.vo.ApplicationVo;
import com.agents.builder.main.domain.vo.ParagraphVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * chat message context
 *
 * @author Angus
 * @date 2024/07/02
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    private String message;

    private ApplicationVo app;

    private Long chatId;

    private Boolean reChat;

    private String optimizedProblem;

    private Long recordId;

    private List<ParagraphVo> documentList;

    private Long startTime;

    private Boolean isTempChat;

    private String contact;

    private Map<String, Object> userParams;

    private Map<String, Object> apiParams;
}
