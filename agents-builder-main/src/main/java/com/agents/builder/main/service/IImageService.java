package com.agents.builder.main.service;

import com.agents.builder.main.domain.Image;
import com.agents.builder.main.domain.vo.ImageVo;
import com.agents.builder.main.domain.bo.ImageBo;
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
public interface IImageService {

    /**
     * 查询
     */
    ImageVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<ImageVo> queryPageList(ImageBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<ImageVo> queryList(ImageBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(ImageBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(ImageBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
