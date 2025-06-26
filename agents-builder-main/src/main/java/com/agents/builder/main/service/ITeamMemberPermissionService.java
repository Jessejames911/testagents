package com.agents.builder.main.service;

import com.agents.builder.common.core.enums.OperateTargetType;
import com.agents.builder.common.core.service.DataScopeService;
import com.agents.builder.main.domain.TeamMemberPermission;
import com.agents.builder.main.domain.vo.TeamMemberPermissionVo;
import com.agents.builder.main.domain.bo.TeamMemberPermissionBo;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Service接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface ITeamMemberPermissionService extends DataScopeService {

    /**
     * 查询
     */
    TeamMemberPermissionVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<TeamMemberPermissionVo> queryPageList(TeamMemberPermissionBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<TeamMemberPermissionVo> queryList(TeamMemberPermissionBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(TeamMemberPermissionBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(TeamMemberPermissionBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    Set<String> getAllPermissions(Long userId);

    List<TeamMemberPermissionVo> getVoByUserId(Long userId);

    Boolean deleteByTargetIds(Collection<Long> targetIds);

    Set<String> getTempPermissions(Long applicationId);

    List<TeamMemberPermissionVo> getUserTargetPermissions(Long userId, OperateTargetType targetType);
}
