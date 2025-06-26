package com.agents.builder.main.service;

import com.agents.builder.main.domain.Problem;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.domain.vo.ProblemVo;
import com.agents.builder.main.domain.bo.ProblemBo;
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
public interface IProblemService {

    /**
     * 查询
     */
    ProblemVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<ProblemVo> queryPageList(ProblemBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<ProblemVo> queryList(ProblemBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(ProblemBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(ProblemBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    Boolean insert(Long datasetId, List<String> problemList);

    List<ProblemVo> getByDatasetId(Collection<Long> datasetIds);

    List<ParagraphVo> getParagraph(Long id);

    Boolean deleteByDatasetId(Collection<Long> datasetId);
}
