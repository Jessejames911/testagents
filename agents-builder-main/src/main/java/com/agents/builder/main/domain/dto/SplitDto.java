package com.agents.builder.main.domain.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SplitDto {

    @NotNull(message = "文档文件不能为空")
    private List<MultipartFile> file;


    private Integer limit;

    @JsonProperty("with_filter")
    private Boolean withFilter=false;

    private List<String> patterns;

    private Long datasetId;

}
