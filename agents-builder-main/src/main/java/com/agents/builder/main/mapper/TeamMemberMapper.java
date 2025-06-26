package com.agents.builder.main.mapper;

import com.agents.builder.main.domain.TeamMember;
import com.agents.builder.main.domain.vo.TeamMemberVo;
import com.agents.builder.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface TeamMemberMapper extends BaseMapperPlus<TeamMember, TeamMemberVo> {

    List<TeamMemberVo> getTeamMembers(@Param("teamId")Long teamId);
}
