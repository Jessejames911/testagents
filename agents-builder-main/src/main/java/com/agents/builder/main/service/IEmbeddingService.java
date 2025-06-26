package com.agents.builder.main.service;

import com.agents.builder.main.domain.Embedding;
import com.agents.builder.main.domain.vo.EmbeddingVo;
import com.agents.builder.main.domain.bo.EmbeddingBo;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * Service接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface IEmbeddingService {

    /**
     * 查询
     */
    EmbeddingVo queryById(String id);

    /**
     * 查询列表
     */
    TableDataInfo<EmbeddingVo> queryPageList(EmbeddingBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<EmbeddingVo> queryList(EmbeddingBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(EmbeddingBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(EmbeddingBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
