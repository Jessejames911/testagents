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
import com.agents.builder.main.domain.bo.ApplicationWorkFlowVersionBo;
import com.agents.builder.main.domain.vo.ApplicationWorkFlowVersionVo;
import com.agents.builder.main.domain.ApplicationWorkFlowVersion;
import com.agents.builder.main.mapper.ApplicationWorkFlowVersionMapper;
import com.agents.builder.main.service.IApplicationWorkFlowVersionService;

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
public class ApplicationWorkFlowVersionServiceImpl implements IApplicationWorkFlowVersionService {

    private final ApplicationWorkFlowVersionMapper baseMapper;

    /**
     * 查询
     */
    @Override
    public ApplicationWorkFlowVersionVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<ApplicationWorkFlowVersionVo> queryPageList(ApplicationWorkFlowVersionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ApplicationWorkFlowVersion> lqw = buildQueryWrapper(bo);
        Page<ApplicationWorkFlowVersionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<ApplicationWorkFlowVersionVo> queryList(ApplicationWorkFlowVersionBo bo) {
        LambdaQueryWrapper<ApplicationWorkFlowVersion> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ApplicationWorkFlowVersion> buildQueryWrapper(ApplicationWorkFlowVersionBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ApplicationWorkFlowVersion> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getApplicationId() != null, ApplicationWorkFlowVersion::getApplicationId, bo.getApplicationId());
        lqw.orderByDesc(ApplicationWorkFlowVersion::getCreateTime);
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(ApplicationWorkFlowVersionBo bo) {
        ApplicationWorkFlowVersion add = MapstructUtils.convert(bo, ApplicationWorkFlowVersion.class);
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
    public Boolean updateByBo(ApplicationWorkFlowVersionBo bo) {
        ApplicationWorkFlowVersion update = MapstructUtils.convert(bo, ApplicationWorkFlowVersion.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ApplicationWorkFlowVersion entity){
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
    public Boolean deleteByAppId(Collection<Long> appIds) {
        if (CollUtil.isEmpty(appIds)){
            return true;
        }
        return baseMapper.delete(new LambdaQueryWrapper<ApplicationWorkFlowVersion>()
                .in(ApplicationWorkFlowVersion::getApplicationId, appIds)) > 0;
    }

    @Override
    public Boolean insert(ApplicationWorkFlowVersion workFlowVersion) {
        return baseMapper.insert(workFlowVersion) > 0;
    }
}
