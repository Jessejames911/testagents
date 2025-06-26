package com.agents.builder.main.service.impl;

import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.agents.builder.main.domain.bo.EmbeddingBo;
import com.agents.builder.main.domain.vo.EmbeddingVo;
import com.agents.builder.main.domain.Embedding;
import com.agents.builder.main.mapper.EmbeddingMapper;
import com.agents.builder.main.service.IEmbeddingService;

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
public class EmbeddingServiceImpl implements IEmbeddingService {

    private final EmbeddingMapper baseMapper;

    /**
     * 查询
     */
    @Override
    public EmbeddingVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<EmbeddingVo> queryPageList(EmbeddingBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Embedding> lqw = buildQueryWrapper(bo);
        Page<EmbeddingVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<EmbeddingVo> queryList(EmbeddingBo bo) {
        LambdaQueryWrapper<Embedding> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Embedding> buildQueryWrapper(EmbeddingBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Embedding> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getSourceId()), Embedding::getSourceId, bo.getSourceId());
        lqw.eq(StringUtils.isNotBlank(bo.getSourceType()), Embedding::getSourceType, bo.getSourceType());
        lqw.eq(bo.getIsActive() != null, Embedding::getIsActive, bo.getIsActive());
        lqw.eq(StringUtils.isNotBlank(bo.getEmbedding()), Embedding::getEmbedding, bo.getEmbedding());
        lqw.eq(bo.getDatasetId() != null, Embedding::getDatasetId, bo.getDatasetId());
        lqw.eq(bo.getDocumentId() != null, Embedding::getDocumentId, bo.getDocumentId());
        lqw.eq(bo.getParagraphId() != null, Embedding::getParagraphId, bo.getParagraphId());
        lqw.eq(StringUtils.isNotBlank(bo.getSearchVector()), Embedding::getSearchVector, bo.getSearchVector());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(EmbeddingBo bo) {
        Embedding add = MapstructUtils.convert(bo, Embedding.class);
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
    public Boolean updateByBo(EmbeddingBo bo) {
        Embedding update = MapstructUtils.convert(bo, Embedding.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Embedding entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
