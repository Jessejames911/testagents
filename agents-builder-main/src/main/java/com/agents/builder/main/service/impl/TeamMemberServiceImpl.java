package com.agents.builder.main.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.agents.builder.common.core.constant.UserConstants;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.agents.builder.main.domain.TeamMember;
import com.agents.builder.main.domain.TeamMemberPermission;
import com.agents.builder.main.domain.User;
import com.agents.builder.main.domain.bo.TeamMemberBo;
import com.agents.builder.main.domain.vo.TeamMemberPermissionVo;
import com.agents.builder.main.domain.vo.TeamMemberVo;
import com.agents.builder.main.mapper.TeamMemberMapper;
import com.agents.builder.main.mapper.TeamMemberPermissionMapper;
import com.agents.builder.main.mapper.UserMapper;
import com.agents.builder.main.service.ITeamMemberService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service业务层处理
 *
 * @author Angus
 * @date 2024-10-31
 */
@RequiredArgsConstructor
@Service
public class TeamMemberServiceImpl implements ITeamMemberService {

    private final TeamMemberMapper baseMapper;

    private final UserMapper userMapper;

    private final TeamMemberPermissionMapper teamMemberPermissionMapper;

    /**
     * 查询
     */
    @Override
    public TeamMemberVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<TeamMemberVo> queryPageList(TeamMemberBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<TeamMember> lqw = buildQueryWrapper(bo);
        Page<TeamMemberVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<TeamMemberVo> queryList(TeamMemberBo bo) {
        LambdaQueryWrapper<TeamMember> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<TeamMember> buildQueryWrapper(TeamMemberBo bo) {

        LambdaQueryWrapper<TeamMember> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getTeamId() != null, TeamMember::getTeamId, bo.getTeamId());
        lqw.eq(bo.getUserId() != null, TeamMember::getUserId, bo.getUserId());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(TeamMemberBo bo) {
        TeamMember add = MapstructUtils.convert(bo, TeamMember.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改
     */
    @Override
    public Boolean updateByBo(TeamMemberBo bo) {
        TeamMember update = MapstructUtils.convert(bo, TeamMember.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(TeamMember entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        if (CollUtil.isEmpty(ids))return true;

        teamMemberPermissionMapper.delete(new LambdaQueryWrapper<TeamMemberPermission>()
                .in(TeamMemberPermission::getMemberId, ids));

        List<TeamMember> teamMembers = baseMapper.selectBatchIds(ids);
        teamMembers.forEach(teamMember->{
            StpUtil.logout(teamMember.getUserId().toString());
        });

        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public List<TeamMember> getByTeamId(Long teamId) {
        return baseMapper.selectList(new LambdaQueryWrapper<TeamMember>()
                .eq(TeamMember::getTeamId, teamId));
    }

    @Override
    public List<TeamMemberVo> getUserTeamMembers(Long teamId) {
        List<TeamMemberVo> members = baseMapper.getTeamMembers(teamId);
        members.forEach(member->member.setType("member"));
        User user = userMapper.selectById(teamId);
        TeamMemberVo memberVo = new TeamMemberVo();
        memberVo.setUsername(user.getUsername());
        memberVo.setEmail(user.getEmail());
        memberVo.setId("root");
        memberVo.setTeamId(teamId);
        memberVo.setUserId(teamId);
        memberVo.setType("manage");
        members.add(memberVo);
        return CollUtil.reverse(members);
    }

    @Override
    public Boolean batchSaveMembers(List<Long> userIdList, Long teamId) {
        List<TeamMemberVo> userTeamMembers = getUserTeamMembers(teamId);


        List<TeamMember> teamMemberList = userIdList.stream().map(userId -> {
            if (UserConstants.SUPER_ADMIN_ID.equals(userId)){
                throw new ServiceException("无法将管理员添加到团队内");
            }
            if (userTeamMembers.contains(userId)){
                return null;
            }
            TeamMember teamMember = new TeamMember();
            teamMember.setTeamId(teamId);
            teamMember.setUserId(userId);
            return teamMember;
        }).filter(Objects::nonNull).collect(Collectors.toList());


        return baseMapper.insertBatch(teamMemberList);
    }



    @Override
    @Transactional
    public Boolean remove(Long id) {

        return deleteWithValidByIds(List.of(id),true);
    }


    @Override
    public Boolean deleteByTeamIds(Collection<Long> teamIdList) {
        if (CollUtil.isEmpty(teamIdList)){
            return true;
        }
        List<Long> memberIds = getByTeamIds(teamIdList).stream().map(TeamMember::getId).collect(Collectors.toList());

        return deleteWithValidByIds(memberIds,true);
    }

    private List<TeamMember> getByTeamIds(Collection<Long> teamIdList) {
        if (CollUtil.isEmpty(teamIdList)){
            return Collections.emptyList();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<TeamMember>()
                .in(TeamMember::getTeamId, teamIdList));
    }

    @Override
    public Boolean deleteByUser(Long userId) {
        List<Long> memberIds = getByUserId(userId).stream().map(TeamMember::getId).collect(Collectors.toList());
        if (CollUtil.isEmpty(memberIds)){
            return true;
        }
        return deleteWithValidByIds(memberIds, true);
    }

    private List<TeamMember> getByUserId(Long userId) {
        return baseMapper.selectList(new LambdaQueryWrapper<TeamMember>()
                .eq(TeamMember::getUserId, userId));
    }


    @Override
    public Map<String, List<TeamMemberPermissionVo>> getRootInfos(Long userId) {
        return teamMemberPermissionMapper.getRootInfos(userId)
                .stream().map(item->{
                    item.setOperate(Map.of("USE",true,"MANAGE",true));
                    return item;
                }).collect(Collectors.groupingBy(TeamMemberPermissionVo::getType));
    }

    @Override
    public Map<String, List<TeamMemberPermissionVo>> getUserInfos(Long userId) {

        return teamMemberPermissionMapper.getUserInfos(LoginHelper.getUserId(),userId).stream().map(item->{
            if (StrUtil.isNotEmpty(item.getOperateStr())){
                item.setOperate(JSON.parseObject(item.getOperateStr(), Map.class));
            }
            return item;
        }).collect(Collectors.groupingBy(TeamMemberPermissionVo::getType));
    }



    @Override
    @Transactional
    public Boolean updatePersmissionByBo(TeamMemberBo bo) {
        TeamMember teamMember = baseMapper.selectById(bo.getId());

        List<TeamMemberPermission> teamMemberPermissionList = bo.getTeamMemberPermissionList()
                .stream().map(item -> {
                    TeamMemberPermission memberPermission = MapstructUtils.convert(item, TeamMemberPermission.class);
                    memberPermission.setMemberId(bo.getId());
                    return memberPermission;
                }).collect(Collectors.toList());
        // 删除权限
        teamMemberPermissionMapper.delete(new LambdaQueryWrapper<TeamMemberPermission>()
                .eq(TeamMemberPermission::getMemberId, bo.getId()));

        teamMemberPermissionMapper.insertBatch(teamMemberPermissionList);

        StpUtil.logout(teamMember.getUserId().toString());
        return true;
    }


}
