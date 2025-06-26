package com.agents.builder.main.mapper;

import com.agents.builder.main.domain.TeamMemberPermission;
import com.agents.builder.main.domain.vo.TeamMemberPermissionVo;
import com.agents.builder.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface TeamMemberPermissionMapper extends BaseMapperPlus<TeamMemberPermission, TeamMemberPermissionVo> {

    List<TeamMemberPermissionVo> getRootInfos(@Param("userId") Long userId);

    List<TeamMemberPermissionVo> getUserInfos(@Param("teamId") Long teamId, @Param("userId") Long userId);

    List<TeamMemberPermissionVo> getUserMainTargetPermission(@Param("userId") Long userId, @Param("targetType") String targetType);

    List<TeamMemberPermissionVo> getUserBaseTargetPermission(@Param("userId") Long userId, @Param("targetType") String targetType);

    List<TeamMemberPermissionVo> getUserMembersPermission(@Param("memberIds") List<Long> memberIds,@Param("userId")Long userId);
}
