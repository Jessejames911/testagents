package com.agents.builder.main.service;

import com.agents.builder.common.core.workflow.LogicFlow;
import com.agents.builder.main.domain.bo.ApplicationBo;
import com.agents.builder.main.domain.vo.ApplicationChatVo;
import com.agents.builder.main.domain.bo.ApplicationChatBo;
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
public interface IApplicationChatService {

    /**
     * 查询
     */
    ApplicationChatVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<ApplicationChatVo> queryPageList(ApplicationChatBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<ApplicationChatVo> queryList(ApplicationChatBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(ApplicationChatBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(ApplicationChatBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    String tempOpen(ApplicationBo bo);

    String clientOpen(Long appId);

    TableDataInfo<ApplicationChatVo> getVoPage(ApplicationChatBo bo, PageQuery pageQuery);

    Boolean deleteByAppId(Collection<Long> appIds);

    String tempWorkflowOpen(LogicFlow workFlow);

    void cleanExpiredChat(Long appId,Integer saveDay);

    String tempOpen(Long appId)
            ;
}
