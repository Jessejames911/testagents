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
import com.agents.builder.main.domain.bo.ApplicationApiKeyBo;
import com.agents.builder.main.domain.vo.ApplicationApiKeyVo;
import com.agents.builder.main.domain.ApplicationApiKey;
import com.agents.builder.main.mapper.ApplicationApiKeyMapper;
import com.agents.builder.main.service.IApplicationApiKeyService;

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
public class ApplicationApiKeyServiceImpl implements IApplicationApiKeyService {

    private final ApplicationApiKeyMapper baseMapper;

    /**
     * 查询
     */
    @Override
    public ApplicationApiKeyVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<ApplicationApiKeyVo> queryPageList(ApplicationApiKeyBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ApplicationApiKey> lqw = buildQueryWrapper(bo);
        Page<ApplicationApiKeyVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<ApplicationApiKeyVo> queryList(ApplicationApiKeyBo bo) {
        LambdaQueryWrapper<ApplicationApiKey> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ApplicationApiKey> buildQueryWrapper(ApplicationApiKeyBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ApplicationApiKey> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getSecretKey()), ApplicationApiKey::getSecretKey, bo.getSecretKey());
        lqw.eq(bo.getIsActive() != null, ApplicationApiKey::getIsActive, bo.getIsActive());
        lqw.eq(bo.getApplicationId() != null, ApplicationApiKey::getApplicationId, bo.getApplicationId());
        lqw.eq(bo.getAllowCrossDomain() != null, ApplicationApiKey::getAllowCrossDomain, bo.getAllowCrossDomain());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(ApplicationApiKeyBo bo) {
        ApplicationApiKey add = MapstructUtils.convert(bo, ApplicationApiKey.class);
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
    public Boolean updateByBo(ApplicationApiKeyBo bo) {
        ApplicationApiKey update = MapstructUtils.convert(bo, ApplicationApiKey.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ApplicationApiKey entity){
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
        return baseMapper.delete(new LambdaQueryWrapper<ApplicationApiKey>()
                .in(ApplicationApiKey::getApplicationId, appIds)) > 0;
    }

    @Override
    public ApplicationApiKeyVo getBySecretKey(String secretKey) {
        return baseMapper.selectVoOne(new LambdaQueryWrapper<ApplicationApiKey>()
                .eq(ApplicationApiKey::getSecretKey, secretKey));
    }
}
