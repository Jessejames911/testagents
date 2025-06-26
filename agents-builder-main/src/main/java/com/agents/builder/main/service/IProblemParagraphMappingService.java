package com.agents.builder.main.service;

import com.agents.builder.main.domain.Problem;
import com.agents.builder.main.domain.ProblemParagraphMapping;
import com.agents.builder.main.domain.bo.ProblemBo;
import com.agents.builder.main.domain.vo.ProblemParagraphMappingVo;
import com.agents.builder.main.domain.bo.ProblemParagraphMappingBo;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.main.domain.vo.ProblemVo;

import java.util.Collection;
import java.util.List;

/**
 * Service接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface IProblemParagraphMappingService {

    /**
     * 查询
     */
    ProblemParagraphMappingVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<ProblemParagraphMappingVo> queryPageList(ProblemParagraphMappingBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<ProblemParagraphMappingVo> queryList(ProblemParagraphMappingBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(ProblemParagraphMappingBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(ProblemParagraphMappingBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    List<ProblemParagraphMappingVo> getByParagraphId(Collection<Long> paragraphId);

    List<ProblemParagraphMappingVo> getByProblemId(Collection<Long> problemId);

    Boolean unAssociation(Long paragraphId, Long problemId, ProblemParagraphMappingBo bo);

    Boolean association(Long paragraphId, Long problemId, ProblemParagraphMappingBo bo);

    Boolean deleteByParagraphId(Collection<Long> paragraphIds);

    Boolean deleteByProblemId(Collection<Long> problemIds);

    Boolean insertBatch(List<ProblemParagraphMapping> mappingList);

    List<ProblemBo> getProblemByParagraphId(List<Long> paragraphIds);
}
