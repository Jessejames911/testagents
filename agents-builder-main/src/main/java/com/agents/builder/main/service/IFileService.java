package com.agents.builder.main.service;

import com.agents.builder.main.domain.File;
import com.agents.builder.main.domain.vo.FileVo;
import com.agents.builder.main.domain.bo.FileBo;
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
public interface IFileService {

    /**
     * 查询
     */
    FileVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<FileVo> queryPageList(FileBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<FileVo> queryList(FileBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(FileBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(FileBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
