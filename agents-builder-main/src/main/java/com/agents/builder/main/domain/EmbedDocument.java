package com.agents.builder.main.domain;

import com.agents.builder.common.web.enums.BusinessType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class EmbedDocument {


    /**
     * 文档片段id
     */
    private Long paragraphId;

    /**
     * 向量库文档id
     */
    private String embedId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 文档id
     */
    private Long documentId;

    /**
     * 是否启用
     */
    private Boolean isActive = true;

    private Long datasetId;

    private BusinessType optType;

    private Long problemId;

    private Long userId;
}
