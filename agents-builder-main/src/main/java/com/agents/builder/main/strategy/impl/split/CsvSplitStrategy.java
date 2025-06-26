package com.agents.builder.main.strategy.impl.split;

import com.agents.builder.main.domain.dto.SplitDto;
import com.agents.builder.main.domain.vo.DocumentVo;
import com.agents.builder.main.enums.DocType;
import com.agents.builder.main.strategy.common.DocSplitCommon;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class CsvSplitStrategy extends DocSplitCommon {
    @Override
    public DocumentVo smartSplit(MultipartFile file) {
        return null;
    }

    @Override
    public DocumentVo split(MultipartFile file, SplitDto dto) {
        return null;
    }

    @Override
    public Set<DocType> docTypes() {
        return Set.of(DocType.CSV);
    }
}
