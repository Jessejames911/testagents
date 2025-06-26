package com.agents.builder.main.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.agents.builder.main.domain.bo.ApplicationDatasetMappingBo;
import com.agents.builder.main.domain.vo.ApplicationDatasetMappingVo;
import com.agents.builder.main.domain.ApplicationDatasetMapping;
import com.agents.builder.main.mapper.ApplicationDatasetMappingMapper;
import com.agents.builder.main.service.IApplicationDatasetMappingService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * Service业务层处理
 *
 * @author Angus
 * @date 2024-10-31
 */
@RequiredArgsConstructor
@Service
public class ApplicationDatasetMappingServiceImpl implements IApplicationDatasetMappingService {

    private final ApplicationDatasetMappingMapper baseMapper;

    /**
     * 查询
     */
    @Override
    public ApplicationDatasetMappingVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<ApplicationDatasetMappingVo> queryPageList(ApplicationDatasetMappingBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ApplicationDatasetMapping> lqw = buildQueryWrapper(bo);
        Page<ApplicationDatasetMappingVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<ApplicationDatasetMappingVo> queryList(ApplicationDatasetMappingBo bo) {
        LambdaQueryWrapper<ApplicationDatasetMapping> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ApplicationDatasetMapping> buildQueryWrapper(ApplicationDatasetMappingBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ApplicationDatasetMapping> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getApplicationId() != null, ApplicationDatasetMapping::getApplicationId, bo.getApplicationId());
        lqw.eq(bo.getDatasetId() != null, ApplicationDatasetMapping::getDatasetId, bo.getDatasetId());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(ApplicationDatasetMappingBo bo) {
        ApplicationDatasetMapping add = MapstructUtils.convert(bo, ApplicationDatasetMapping.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改
     */
    @Override
    public Boolean updateByBo(ApplicationDatasetMappingBo bo) {
        ApplicationDatasetMapping update = MapstructUtils.convert(bo, ApplicationDatasetMapping.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ApplicationDatasetMapping entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public Boolean saveBatch(List<ApplicationDatasetMapping> datasetMappings) {
        return baseMapper.insertBatch(datasetMappings);
    }

    @Override
    public Boolean deleteByAppId(Collection<Long> appIds) {
        if (CollUtil.isEmpty(appIds)){
            return true;
        }
        return baseMapper.delete(new LambdaQueryWrapper<ApplicationDatasetMapping>()
                .in(ApplicationDatasetMapping::getApplicationId, appIds)) > 0;
    }

    @Override
    public Boolean deleteByDatasetId(Collection<Long> datasetIds) {
        if (CollUtil.isEmpty(datasetIds)){
            return true;
        }
        return baseMapper.delete(new LambdaQueryWrapper<ApplicationDatasetMapping>()
                .in(ApplicationDatasetMapping::getDatasetId, datasetIds)) > 0;
    }

    @Override
    public List<ApplicationDatasetMappingVo> getByDatasetId(Long datasetId) {
        return baseMapper.selectVoList(new LambdaQueryWrapper<ApplicationDatasetMapping>()
                .eq(ApplicationDatasetMapping::getDatasetId,datasetId));
    }

    @Override
    public List<ApplicationDatasetMappingVo> getByAppId(Long appId) {
        return baseMapper.selectVoList(new LambdaQueryWrapper<ApplicationDatasetMapping>()
                .eq(ApplicationDatasetMapping::getApplicationId,appId));
    }
}
