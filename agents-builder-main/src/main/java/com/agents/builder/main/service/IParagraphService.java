package com.agents.builder.main.service;

import com.agents.builder.main.domain.EmbedDocument;
import com.agents.builder.main.domain.Paragraph;
import com.agents.builder.main.domain.bo.ProblemBo;
import com.agents.builder.main.domain.dto.DocGenerateRelatedDto;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.domain.bo.ParagraphBo;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.main.domain.vo.ProblemVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import java.util.Collection;
import java.util.List;

/**
 * Service接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface IParagraphService {

    /**
     * 查询
     */
    ParagraphVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<ParagraphVo> queryPageList(ParagraphBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<ParagraphVo> queryList(ParagraphBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(ParagraphBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(ParagraphBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    List<ProblemVo> getProblem(Long id);

    Boolean addProblem(ProblemBo bo);

    Boolean insertBatch(List<Paragraph> paragraphs);

    Boolean deleteByDocId(Collection<Long> docIds);

    List<ParagraphVo> selectVoBatchIds(Collection<Long> ids);

    Boolean insertBatchByBo(List<ParagraphBo> paragraphBos);

    Boolean updateIsActiveByDocId(Long docId, Boolean isActive);

    List<EmbedDocument> getEmbedDocuments(List<Long> documentIds);

    Boolean update(Wrapper<Paragraph> updateWrapper);

    List<Paragraph> getByDocumentIds(List<Long> documentIdList);

    Boolean batchGenerateRelated(DocGenerateRelatedDto dto);

    Boolean migrate(List<Long> paragraphIdList,Long sourceDataset,Long targetDataset,Long sourceDocument,Long targetDocument);
}
