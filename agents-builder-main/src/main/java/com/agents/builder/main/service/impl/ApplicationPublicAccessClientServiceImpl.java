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
import com.agents.builder.main.domain.bo.ApplicationPublicAccessClientBo;
import com.agents.builder.main.domain.vo.ApplicationPublicAccessClientVo;
import com.agents.builder.main.domain.ApplicationPublicAccessClient;
import com.agents.builder.main.mapper.ApplicationPublicAccessClientMapper;
import com.agents.builder.main.service.IApplicationPublicAccessClientService;

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
public class ApplicationPublicAccessClientServiceImpl implements IApplicationPublicAccessClientService {

    private final ApplicationPublicAccessClientMapper baseMapper;

    /**
     * 查询
     */
    @Override
    public ApplicationPublicAccessClientVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<ApplicationPublicAccessClientVo> queryPageList(ApplicationPublicAccessClientBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ApplicationPublicAccessClient> lqw = buildQueryWrapper(bo);
        Page<ApplicationPublicAccessClientVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<ApplicationPublicAccessClientVo> queryList(ApplicationPublicAccessClientBo bo) {
        LambdaQueryWrapper<ApplicationPublicAccessClient> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ApplicationPublicAccessClient> buildQueryWrapper(ApplicationPublicAccessClientBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ApplicationPublicAccessClient> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getAccessNum() != null, ApplicationPublicAccessClient::getAccessNum, bo.getAccessNum());
        lqw.eq(bo.getIntradayAccessNum() != null, ApplicationPublicAccessClient::getIntradayAccessNum, bo.getIntradayAccessNum());
        lqw.eq(bo.getApplicationId() != null, ApplicationPublicAccessClient::getApplicationId, bo.getApplicationId());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(ApplicationPublicAccessClientBo bo) {
        ApplicationPublicAccessClient add = MapstructUtils.convert(bo, ApplicationPublicAccessClient.class);
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
    public Boolean updateByBo(ApplicationPublicAccessClientBo bo) {
        ApplicationPublicAccessClient update = MapstructUtils.convert(bo, ApplicationPublicAccessClient.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ApplicationPublicAccessClient entity){
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
        return baseMapper.delete(new LambdaQueryWrapper<ApplicationPublicAccessClient>()
                .in(ApplicationPublicAccessClient::getApplicationId, appIds)) > 0;
    }
}
