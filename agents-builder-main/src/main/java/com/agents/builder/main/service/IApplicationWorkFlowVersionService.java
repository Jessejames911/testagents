package com.agents.builder.main.service;

import com.agents.builder.main.domain.ApplicationWorkFlowVersion;
import com.agents.builder.main.domain.vo.ApplicationWorkFlowVersionVo;
import com.agents.builder.main.domain.bo.ApplicationWorkFlowVersionBo;
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
public interface IApplicationWorkFlowVersionService {

    /**
     * 查询
     */
    ApplicationWorkFlowVersionVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<ApplicationWorkFlowVersionVo> queryPageList(ApplicationWorkFlowVersionBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<ApplicationWorkFlowVersionVo> queryList(ApplicationWorkFlowVersionBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(ApplicationWorkFlowVersionBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(ApplicationWorkFlowVersionBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    Boolean deleteByAppId(Collection<Long> appIds);

    Boolean insert(ApplicationWorkFlowVersion workFlowVersion);
}
