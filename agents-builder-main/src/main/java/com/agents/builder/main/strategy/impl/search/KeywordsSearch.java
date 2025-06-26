package com.agents.builder.main.strategy.impl.search;

import cn.hutool.core.lang.Assert;
import com.agents.builder.main.domain.dto.SearchDto;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.enums.SearchMode;
import com.agents.builder.main.mapper.ParagraphMapper;
import com.agents.builder.main.strategy.common.SearchCommon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeywordsSearch extends SearchCommon {

    private final ParagraphMapper paragraphMapper;

    @Override
    public List<ParagraphVo> search(SearchDto dto) {
        Assert.notEmpty(dto.getDatasetIdList(),"知识库id不能为空");
        if (dto.getSimilarity()==null){
            dto.setSimilarity(0.0);
        }
       return paragraphMapper.match(dto.getQuery_text(),dto.getDatasetIdList(),dto.getTop_number(), dto.getSimilarity()* 100);
    }

    @Override
    public SearchMode mode() {
        return SearchMode.KEYWORDS;
    }
}
