package com.agents.builder.main.service;

import com.agents.builder.main.domain.ApplicationAccessToken;
import com.agents.builder.main.domain.vo.ApplicationAccessTokenVo;
import com.agents.builder.main.domain.bo.ApplicationAccessTokenBo;
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
public interface IApplicationAccessTokenService {

    /**
     * 查询
     */
    ApplicationAccessTokenVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<ApplicationAccessTokenVo> queryPageList(ApplicationAccessTokenBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<ApplicationAccessTokenVo> queryList(ApplicationAccessTokenBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(ApplicationAccessTokenBo bo);

    /**
     * 修改
     */
    ApplicationAccessTokenVo updateByBo(ApplicationAccessTokenBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    ApplicationAccessToken getByToken(String accessToken);

    Boolean insertAppDefault(Long appId);

    Boolean deleteByAppId(Collection<Long> appIds);

    void checkAccessNum(Long clientId, Long appId);
}
