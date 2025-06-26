package com.agents.builder.main.service;

import com.agents.builder.main.domain.TeamMember;
import com.agents.builder.main.domain.vo.TeamMemberPermissionVo;
import com.agents.builder.main.domain.vo.TeamMemberVo;
import com.agents.builder.main.domain.bo.TeamMemberBo;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Service接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface ITeamMemberService {

    /**
     * 查询
     */
    TeamMemberVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<TeamMemberVo> queryPageList(TeamMemberBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<TeamMemberVo> queryList(TeamMemberBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(TeamMemberBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(TeamMemberBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    List<TeamMember> getByTeamId(Long userId);

    List<TeamMemberVo> getUserTeamMembers(Long userId);


    Boolean batchSaveMembers(List<Long> userIdList, Long teamId);

    Map<String, List<TeamMemberPermissionVo>> getRootInfos(Long userId);

    Boolean updatePersmissionByBo(TeamMemberBo bo);

    Map<String, List<TeamMemberPermissionVo>> getUserInfos(Long userId);

    Boolean remove(Long id);

    Boolean deleteByTeamIds(Collection<Long> teamIdList);

    Boolean deleteByUser(Long userId);
}
