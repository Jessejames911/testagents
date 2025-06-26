package com.agents.builder.main.service;

import com.agents.builder.main.domain.ApplicationApiKey;
import com.agents.builder.main.domain.vo.ApplicationApiKeyVo;
import com.agents.builder.main.domain.bo.ApplicationApiKeyBo;
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
public interface IApplicationApiKeyService {

    /**
     * 查询
     */
    ApplicationApiKeyVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<ApplicationApiKeyVo> queryPageList(ApplicationApiKeyBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<ApplicationApiKeyVo> queryList(ApplicationApiKeyBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(ApplicationApiKeyBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(ApplicationApiKeyBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    Boolean deleteByAppId(Collection<Long> appIds);

    ApplicationApiKeyVo getBySecretKey(String secretKey);
}
