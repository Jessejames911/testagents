package com.agents.builder.main.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DocImproveDto {

    private String content;

    @JsonProperty("problem_text")
    private String problemText;

    private String title;

    private Long recordId;

    private Long datasetId;

    private Long documentId;
}
