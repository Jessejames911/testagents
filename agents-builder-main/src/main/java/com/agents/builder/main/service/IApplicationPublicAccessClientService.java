package com.agents.builder.main.service;

import com.agents.builder.main.domain.ApplicationPublicAccessClient;
import com.agents.builder.main.domain.vo.ApplicationPublicAccessClientVo;
import com.agents.builder.main.domain.bo.ApplicationPublicAccessClientBo;
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
public interface IApplicationPublicAccessClientService {

    /**
     * 查询
     */
    ApplicationPublicAccessClientVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<ApplicationPublicAccessClientVo> queryPageList(ApplicationPublicAccessClientBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<ApplicationPublicAccessClientVo> queryList(ApplicationPublicAccessClientBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(ApplicationPublicAccessClientBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(ApplicationPublicAccessClientBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    Boolean deleteByAppId(Collection<Long> appIds);
}
