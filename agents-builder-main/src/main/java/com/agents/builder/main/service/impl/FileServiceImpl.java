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
import com.agents.builder.main.domain.bo.FileBo;
import com.agents.builder.main.domain.vo.FileVo;
import com.agents.builder.main.domain.File;
import com.agents.builder.main.mapper.FileMapper;
import com.agents.builder.main.service.IFileService;

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
public class FileServiceImpl implements IFileService {

    private final FileMapper baseMapper;

    /**
     * 查询
     */
    @Override
    public FileVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<FileVo> queryPageList(FileBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<File> lqw = buildQueryWrapper(bo);
        Page<FileVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<FileVo> queryList(FileBo bo) {
        LambdaQueryWrapper<File> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<File> buildQueryWrapper(FileBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<File> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getFileName()), File::getFileName, bo.getFileName());
        lqw.eq(bo.getLoid() != null, File::getLoid, bo.getLoid());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(FileBo bo) {
        File add = MapstructUtils.convert(bo, File.class);
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
    public Boolean updateByBo(FileBo bo) {
        File update = MapstructUtils.convert(bo, File.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(File entity){
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
