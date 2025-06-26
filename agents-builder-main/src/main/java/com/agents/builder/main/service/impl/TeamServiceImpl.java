package com.agents.builder.main.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.main.domain.Team;
import com.agents.builder.main.domain.bo.TeamBo;
import com.agents.builder.main.domain.vo.TeamVo;
import com.agents.builder.main.mapper.TeamMapper;
import com.agents.builder.main.service.ITeamMemberService;
import com.agents.builder.main.service.ITeamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service业务层处理
 *
 * @author Angus
 * @date 2024-10-31
 */
@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements ITeamService {

    private final TeamMapper baseMapper;

    private final ITeamMemberService teamMemberService;

    /**
     * 查询
     */
    @Override
    public TeamVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<TeamVo> queryPageList(TeamBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Team> lqw = buildQueryWrapper(bo);
        Page<TeamVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<TeamVo> queryList(TeamBo bo) {
        LambdaQueryWrapper<Team> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Team> buildQueryWrapper(TeamBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Team> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, Team::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getName()), Team::getName, bo.getName());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(TeamBo bo) {
        Team add = MapstructUtils.convert(bo, Team.class);
        validEntityBeforeSave(add);
        return baseMapper.insert(add) > 0;
    }

    /**
     * 修改
     */
    @Override
    public Boolean updateByBo(TeamBo bo) {
        Team update = MapstructUtils.convert(bo, Team.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Team entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        // 刪除团队成员
        teamMemberService.deleteByTeamIds(ids);

        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public Boolean deleteByUser(Long userId) {
        List<Long> teamIds = getByUser(userId).stream().map(Team::getUserId).collect(Collectors.toList());
        if (CollUtil.isEmpty(teamIds)){
            return true;
        }
        return deleteWithValidByIds(teamIds,true);
    }

    private List<Team> getByUser(Long userId) {
        return baseMapper.selectList(new LambdaQueryWrapper<Team>()
                .eq(Team::getUserId,userId));
    }


}
