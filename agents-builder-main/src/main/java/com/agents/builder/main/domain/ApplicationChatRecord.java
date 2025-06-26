package com.agents.builder.main.domain;

import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.io.Serial;
import java.util.List;
import java.util.Map;

/**
 * 对象 application_chat_record
 *
 * @author Angus
 * @date 2024-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "application_chat_record",autoResultMap = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationChatRecord extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
    private String voteStatus;

    /**
     *
     */
    private String problemText;

    /**
     *
     */
    private String answerText;

    /**
     *
     */
    private Long messageTokens;

    /**
     *
     */
    private Long answerTokens;

    /**
     *
     */
    private Long cost;

    /**
     *
     */
    @TableField(value = "details",
            typeHandler = JacksonTypeHandler.class)
    private List<Map<String, Object>> details;




    /**
     *
     */
    @TableField(value = "improve_paragraph_id_list",
            typeHandler = JacksonTypeHandler.class)
    private List<Long> improveParagraphIdList;

    /**
     *
     */
    private Double runTime;

    /**
     *
     */
    private Long recordIndex;

    /**
     *
     */
    private Long chatId;

    private Long applicationId;


}
