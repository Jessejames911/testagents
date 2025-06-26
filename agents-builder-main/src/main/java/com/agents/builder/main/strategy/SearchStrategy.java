package com.agents.builder.main.strategy;

import com.agents.builder.main.domain.dto.SearchDto;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.enums.SearchMode;

import java.util.List;

public interface SearchStrategy {

    List<ParagraphVo> search(SearchDto dto);

    SearchMode mode();
}
