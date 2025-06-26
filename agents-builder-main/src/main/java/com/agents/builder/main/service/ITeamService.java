package com.agents.builder.main.service;

import com.agents.builder.main.domain.Team;
import com.agents.builder.main.domain.vo.TeamVo;
import com.agents.builder.main.domain.bo.TeamBo;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.main.domain.vo.UserVo;

import java.util.Collection;
import java.util.List;

/**
 * Service接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface ITeamService {

    /**
     * 查询
     */
    TeamVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<TeamVo> queryPageList(TeamBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<TeamVo> queryList(TeamBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(TeamBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(TeamBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    Boolean deleteByUser(Long userId);
}
