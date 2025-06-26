package com.agents.builder.main.service;

import com.agents.builder.main.domain.vo.ModelFormVo;
import com.agents.builder.main.domain.vo.ModelVo;
import com.agents.builder.main.domain.bo.ModelBo;
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
public interface IModelService {

    /**
     * 查询
     */
    ModelVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<ModelVo> queryPageList(ModelBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<ModelVo> queryList(ModelBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(ModelBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(ModelBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    List<ModelFormVo> getModelForms(ModelBo bo);

    List<ModelVo> getActivedByIdAndType(List<Long> modelIdList, String type);

    List<ModelFormVo> getModelParamsForm(Long modelId);

    Boolean deleteByUser(Long userId);
}
