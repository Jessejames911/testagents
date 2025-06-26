package com.agents.builder.main.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchDto {

    @JsonProperty("query_text")
    private String query_text;

    @JsonProperty("top_number")
    private Integer top_number;

    @JsonProperty("similarity")
    private Double similarity;

    @JsonProperty("search_mode")
    private String search_mode;

    @JsonProperty("application_id")
    private Long application_id;

    private List<Long> datasetIdList;

    private int maxParagraphCharNumber;
}
