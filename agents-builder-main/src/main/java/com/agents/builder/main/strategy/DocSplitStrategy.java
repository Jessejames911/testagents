package com.agents.builder.main.strategy;

import com.agents.builder.main.domain.dto.SplitDto;
import com.agents.builder.main.domain.vo.DocumentVo;
import com.agents.builder.main.enums.DocType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface DocSplitStrategy {


    DocumentVo smartSplit(MultipartFile file);

    DocumentVo split(MultipartFile file,SplitDto dto);

    Set<DocType> docTypes();
}
