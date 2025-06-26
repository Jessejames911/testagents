package com.agents.builder.main.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.agents.builder.common.ai.strategy.EmbeddingModelBuilder;
import com.agents.builder.common.ai.strategy.context.EmbeddingModelBuilderContext;
import com.agents.builder.common.ai.vector.VectorStoreBuilder;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.exception.StreamException;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.main.domain.dto.SearchDto;
import com.agents.builder.main.domain.vo.*;
import com.agents.builder.main.mapper.ApplicationMapper;
import com.agents.builder.main.service.*;
import com.agents.builder.main.strategy.context.SearchContext;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import com.agents.builder.main.domain.bo.DatasetBo;
import com.agents.builder.main.domain.Dataset;
import com.agents.builder.main.mapper.DatasetMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Service业务层处理
 *
 * @author Angus
 * @date 2024-10-31
 */
@RequiredArgsConstructor
@Service
public class DatasetServiceImpl implements IDatasetService {

    private final DatasetMapper baseMapper;

    private final IProblemService problemService;

    private final IDocumentService documentService;

    private final IApplicationDatasetMappingService applicationDatasetMappingService;

    private final VectorStoreBuilder vectorStoreBuilder;

    private final IModelService modelService;

    private final EmbeddingModelBuilderContext embeddingModelBuilderContext;

    private final ApplicationMapper applicationMapper;

    private final SearchContext searchContext;

    private final ITeamMemberPermissionService teamMemberPermissionService;
    /**
     * 查询
     */
    @Override
    public DatasetVo queryById(Long id){
        DatasetVo datasetVo = baseMapper.selectVoById(id);
        List<Long> appIdList = applicationDatasetMappingService.getByDatasetId(id).stream().map(ApplicationDatasetMappingVo::getApplicationId).collect(Collectors.toList());
        datasetVo.setApplicationIdList(appIdList);
        datasetVo.setApplicationMappingCount(appIdList.size());
        return datasetVo;

    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<DatasetVo> queryPageList(DatasetBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Dataset> lqw = buildQueryWrapper(bo);
        Page<DatasetVo> result = baseMapper.getVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<DatasetVo> queryList(DatasetBo bo) {
        LambdaQueryWrapper<Dataset> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Dataset> buildQueryWrapper(DatasetBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Dataset> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), Dataset::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), Dataset::getDescription, bo.getDescription());
        lqw.eq(bo.getType()!=null, Dataset::getType, bo.getType());
        lqw.eq(bo.getEmbeddingModeId() != null, Dataset::getEmbeddingModeId, bo.getEmbeddingModeId());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public DatasetVo insertByBo(DatasetBo bo) {
        ModelVo modelVo = modelService.queryById(bo.getEmbeddingModeId());
        Assert.notNull(modelVo, "embedding model not found");
        long id = IdWorker.getId();
        bo.setId(id);
        EmbeddingModelBuilder builder = embeddingModelBuilderContext.getService(modelVo.getProvider(), () -> new StreamException("未找到匹配的模型构建器"));
        EmbeddingModel embeddingModel = builder.build(modelVo.getCredential().getApiKey(), modelVo.getCredential().getApiBase(), modelVo.getModelName());
        // 创建向量库
        vectorStoreBuilder.initialize(embeddingModel,"text_"+id,null);

        Dataset add = MapstructUtils.convert(bo, Dataset.class);
        validEntityBeforeSave(add);

        baseMapper.insert(add);
        return queryById(add.getId());
    }

    /**
     * 修改
     */
    @Override
    public Boolean updateByBo(DatasetBo bo) {
        Dataset update = MapstructUtils.convert(bo, Dataset.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Dataset entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        boolean delete = baseMapper.deleteBatchIds(ids) > 0;
        if (!delete)return false;
        // 删除应用关联
        applicationDatasetMappingService.deleteByDatasetId(ids);
        // 删除所有文档
        documentService.deleteByDatasetId(ids);
        // 删除所有问题
        problemService.deleteByDatasetId(ids);
        ids.forEach(id->vectorStoreBuilder.deleteCollection("text_"+id));
        // 删除权限
        teamMemberPermissionService.deleteByTargetIds(ids);
        return delete;
    }

    @Override
    public List<ProblemVo> getProblem(Long id) {
        return problemService.getByDatasetId(List.of(id));
    }

    @Override
    public List<DatasetVo> selectVoBatchIds(List<Long> datasetIdList) {
        if (CollUtil.isEmpty(datasetIdList)){
            return Collections.emptyList();
        }
        return baseMapper.selectVoBatchIds(datasetIdList);
    }

    @Override
    public List<ApplicationVo> getApplication(Long datasetId) {
        List<Long> appIds = applicationDatasetMappingService.getByDatasetId(datasetId).stream().map(ApplicationDatasetMappingVo::getApplicationId).collect(Collectors.toList());
        if (CollUtil.isEmpty(appIds)){
            return Collections.emptyList();
        }
        return applicationMapper.selectVoBatchIds(appIds);
    }

    @Override
    public List<ParagraphVo> hitTest(SearchDto dto) {
        return searchContext.getService(dto.getSearch_mode(),()->new ServiceException("暂不支持此检索模式")).search(dto);
    }

    @Override
    public Boolean reEmbedding(Long id) {
        return documentService.reEmbeddingByDatasetId(id);
    }

    @Override
    public Boolean deleteByUser(Long userId) {
        List<Long> datasetIds = getByUser(userId).stream().map(Dataset::getId).collect(Collectors.toList());
        if (CollUtil.isEmpty(datasetIds)){
            return true;
        }
        return deleteWithValidByIds(datasetIds,true);
    }

    private List<Dataset> getByUser(Long userId) {
        return baseMapper.selectList(new LambdaQueryWrapper<Dataset>()
                .eq(Dataset::getCreateBy,userId));
    }
}
