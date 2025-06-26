package com.agents.builder.main.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DocGenerateRelatedDto {

    @JsonProperty("document_id_list")
    private List<Long> documentIdList;

    @JsonProperty("paragraph_id_list")
    private List<Long> paragraphIdList;

    @JsonProperty("model_id")
    private Long modelId;

    private String prompt;

    private Long documentId;

    private Long datasetId;
}
