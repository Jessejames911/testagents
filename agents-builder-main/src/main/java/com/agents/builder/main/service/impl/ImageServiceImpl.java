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
import com.agents.builder.main.domain.bo.ImageBo;
import com.agents.builder.main.domain.vo.ImageVo;
import com.agents.builder.main.domain.Image;
import com.agents.builder.main.mapper.ImageMapper;
import com.agents.builder.main.service.IImageService;

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
public class ImageServiceImpl implements IImageService {

    private final ImageMapper baseMapper;

    /**
     * 查询
     */
    @Override
    public ImageVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<ImageVo> queryPageList(ImageBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Image> lqw = buildQueryWrapper(bo);
        Page<ImageVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<ImageVo> queryList(ImageBo bo) {
        LambdaQueryWrapper<Image> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Image> buildQueryWrapper(ImageBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Image> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getImage()), Image::getImage, bo.getImage());
        lqw.like(StringUtils.isNotBlank(bo.getImageName()), Image::getImageName, bo.getImageName());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(ImageBo bo) {
        Image add = MapstructUtils.convert(bo, Image.class);
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
    public Boolean updateByBo(ImageBo bo) {
        Image update = MapstructUtils.convert(bo, Image.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Image entity){
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
}
