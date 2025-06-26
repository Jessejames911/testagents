package com.agents.builder.main.service;

import com.agents.builder.main.domain.FunctionLib;
import com.agents.builder.main.domain.dto.FunctionDebugDto;
import com.agents.builder.main.domain.vo.FunctionDebugVo;
import com.agents.builder.main.domain.vo.FunctionLibVo;
import com.agents.builder.main.domain.bo.FunctionLibBo;
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
public interface IFunctionLibService {

    /**
     * 查询
     */
    FunctionLibVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<FunctionLibVo> queryPageList(FunctionLibBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<FunctionLibVo> queryList(FunctionLibBo bo);

    /**
     * 新增
     */
    FunctionLibVo insertByBo(FunctionLibBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(FunctionLibBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    Object debug(FunctionDebugDto dto);

    FunctionDebugVo pylint(FunctionDebugDto dto);

    Boolean deleteByUser(Long userId);
}
