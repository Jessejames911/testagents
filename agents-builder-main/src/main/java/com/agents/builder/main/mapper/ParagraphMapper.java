package com.agents.builder.main.mapper;

import com.agents.builder.main.domain.Paragraph;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * Mapper接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface ParagraphMapper extends BaseMapperPlus<Paragraph, ParagraphVo> {

    List<ParagraphVo> selectVoByIds(@Param("ids") Collection<Long> ids);

    List<ParagraphVo> match(@Param("keywords") String keywords, @Param("datasetIds")List<Long> datasetIds,@Param("topN") Integer topN,@Param("similarity") Double similarity);
}
