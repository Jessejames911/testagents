package com.agents.builder.main.service;

import com.agents.builder.main.domain.ApplicationDatasetMapping;
import com.agents.builder.main.domain.vo.ApplicationDatasetMappingVo;
import com.agents.builder.main.domain.bo.ApplicationDatasetMappingBo;
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
public interface IApplicationDatasetMappingService {

    /**
     * 查询
     */
    ApplicationDatasetMappingVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<ApplicationDatasetMappingVo> queryPageList(ApplicationDatasetMappingBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<ApplicationDatasetMappingVo> queryList(ApplicationDatasetMappingBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(ApplicationDatasetMappingBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(ApplicationDatasetMappingBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    Boolean saveBatch(List<ApplicationDatasetMapping> datasetMappings);

    Boolean deleteByAppId(Collection<Long> appIds);

    Boolean deleteByDatasetId(Collection<Long> datasetIds);

    List<ApplicationDatasetMappingVo> getByDatasetId(Long datasetId);

    List<ApplicationDatasetMappingVo> getByAppId(Long appId);
}
