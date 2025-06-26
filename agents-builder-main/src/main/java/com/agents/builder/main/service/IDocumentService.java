package com.agents.builder.main.service;

import com.agents.builder.common.core.domain.model.KVObj;
import com.agents.builder.main.domain.Document;
import com.agents.builder.main.domain.dto.DocGenerateRelatedDto;
import com.agents.builder.main.domain.dto.SplitDto;
import com.agents.builder.main.domain.vo.DocumentVo;
import com.agents.builder.main.domain.bo.DocumentBo;
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
public interface IDocumentService {

    /**
     * 查询
     */
    DocumentVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<DocumentVo> queryPageList(DocumentBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<DocumentVo> queryList(DocumentBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(DocumentBo bo);

    /**
     * 修改
     */
    DocumentVo updateByBo(DocumentBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    List<DocumentVo> split(SplitDto dto);

    Boolean insertBatchByBo(List<DocumentBo> boList);

    Boolean deleteByDatasetId(Collection<Long> datasetIds);

    Boolean refresh(Long id,Long datasetId);

    Boolean reEmbeddingByDatasetId(Long datasetId);

    Boolean batchRefresh(List<Long> idList, Long datasetId);

    Boolean migrate(List<Long> documentIdList, Long datasetId, Long target);

    Boolean batchHitHandling(DocumentBo bo);

    Boolean batchGenerateRelated(DocGenerateRelatedDto dto);

    List<KVObj> getSplitPattern();

    List<DocumentVo> importTable(SplitDto dto);

    List<DocumentVo> importQa(SplitDto dto);
}
