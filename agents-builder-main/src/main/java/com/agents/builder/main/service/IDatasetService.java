package com.agents.builder.main.service;

import com.agents.builder.main.domain.Dataset;
import com.agents.builder.main.domain.dto.SearchDto;
import com.agents.builder.main.domain.vo.ApplicationVo;
import com.agents.builder.main.domain.vo.DatasetVo;
import com.agents.builder.main.domain.bo.DatasetBo;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.domain.vo.ProblemVo;

import java.util.Collection;
import java.util.List;

/**
 * Service接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface IDatasetService {

    /**
     * 查询
     */
    DatasetVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<DatasetVo> queryPageList(DatasetBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<DatasetVo> queryList(DatasetBo bo);

    /**
     * 新增
     */
    DatasetVo insertByBo(DatasetBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(DatasetBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    List<ProblemVo> getProblem(Long id);

    List<DatasetVo> selectVoBatchIds(List<Long> datasetIdList);

    List<ApplicationVo> getApplication(Long datasetId);

    List<ParagraphVo> hitTest(SearchDto dto);

    Boolean reEmbedding(Long id);

    Boolean deleteByUser(Long userId);
}
