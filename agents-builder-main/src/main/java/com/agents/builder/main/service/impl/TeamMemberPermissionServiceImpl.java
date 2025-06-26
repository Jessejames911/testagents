package com.agents.builder.main.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.core.enums.OperateTargetType;
import com.agents.builder.common.core.enums.OperateType;
import com.agents.builder.common.core.enums.PermissionOperate;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.agents.builder.main.domain.TeamMember;
import com.agents.builder.main.mapper.TeamMemberMapper;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.agents.builder.main.domain.bo.TeamMemberPermissionBo;
import com.agents.builder.main.domain.vo.TeamMemberPermissionVo;
import com.agents.builder.main.domain.TeamMemberPermission;
import com.agents.builder.main.mapper.TeamMemberPermissionMapper;
import com.agents.builder.main.service.ITeamMemberPermissionService;

import java.util.*;
import java.util.stream.Collectors;

import static com.agents.builder.common.core.enums.PermissionOperate.getAllKeys;
import static com.agents.builder.common.core.enums.PermissionOperate.getCommonUserOptions;

/**
 * Service业务层处理
 *
 * @author Angus
 * @date 2024-10-31
 */
@RequiredArgsConstructor
@Service
public class TeamMemberPermissionServiceImpl implements ITeamMemberPermissionService {

    private final TeamMemberPermissionMapper baseMapper;

    private final TeamMemberMapper teamMemberMapper;

    /**
     * 查询
     */
    @Override
    public TeamMemberPermissionVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<TeamMemberPermissionVo> queryPageList(TeamMemberPermissionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<TeamMemberPermission> lqw = buildQueryWrapper(bo);
        Page<TeamMemberPermissionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<TeamMemberPermissionVo> queryList(TeamMemberPermissionBo bo) {
        LambdaQueryWrapper<TeamMemberPermission> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<TeamMemberPermission> buildQueryWrapper(TeamMemberPermissionBo bo) {

        LambdaQueryWrapper<TeamMemberPermission> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getAuthTargetType()), TeamMemberPermission::getAuthTargetType, bo.getAuthTargetType());
        lqw.eq(bo.getMemberId()!=null, TeamMemberPermission::getMemberId, bo.getMemberId());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(TeamMemberPermissionBo bo) {
        TeamMemberPermission add = MapstructUtils.convert(bo, TeamMemberPermission.class);
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
    public Boolean updateByBo(TeamMemberPermissionBo bo) {
        TeamMemberPermission update = MapstructUtils.convert(bo, TeamMemberPermission.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(TeamMemberPermission entity){
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
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public String getUserCustom(Long userId, String targetType, boolean isSelect) {
        return null;
    }


    @Override
    public Set<String> getTempPermissions(Long applicationId) {
        return Set.of(OperateTargetType.APP.getKey()+ ":" + OperateType.USE + ":" + applicationId,
                PermissionOperate.APPLICATION_READ.getKey());
    }

    @Override
    public List<TeamMemberPermissionVo> getUserTargetPermissions(Long userId, OperateTargetType targetType) {
        List<Long> memberIdList = teamMemberMapper.selectList(new LambdaQueryWrapper<TeamMember>()
                        .eq(TeamMember::getTeamId, userId)
                        .or().eq(TeamMember::getUserId, userId))
                .stream().map(TeamMember::getId).collect(Collectors.toList());

        if (CollUtil.isEmpty(memberIdList)){
            return Collections.emptyList();
        }

        return baseMapper.selectVoList(new LambdaQueryWrapper<TeamMemberPermission>()
                .in(TeamMemberPermission::getMemberId, memberIdList)
                .eq(TeamMemberPermission::getAuthTargetType, targetType.getKey()));
    }

    @Override
    public Set<String> getAllPermissions(Long userId) {

        if (LoginHelper.isSuperAdmin(userId)){
            return getAllKeys();
        }
        List<Long> memberIds = teamMemberMapper.selectList(new LambdaQueryWrapper<TeamMember>()
                .eq(TeamMember::getUserId, userId)).stream().map(item -> item.getId()).collect(Collectors.toList());
        memberIds.add(-1L);
        List<TeamMemberPermissionVo> permissions = baseMapper.getUserMembersPermission(memberIds,userId);
        Set<String> userPermissions = permissions.stream().flatMap(item -> generateOprateList((Map<String, Boolean>)JSONObject.parseObject(item.getOperateStr(), Map.class)).stream().map(opt -> item.getAuthTargetType() + ":" + opt + ":" + item.getTarget())).collect(Collectors.toSet());

        userPermissions.addAll(getCommonUserOptions());

        return userPermissions;
    }

    private List<String> generateOprateList(Map<String, Boolean> operate) {
        List<String> list = new ArrayList<>();
        if (CollUtil.isEmpty(operate))return list;
        operate.forEach((k,v)->{
            if (v){
                list.add(k);
            }
        });
        return list;
    }

    @Override
    public List<TeamMemberPermissionVo> getVoByUserId(Long userId) {
        return baseMapper.selectVoList(new LambdaQueryWrapper<TeamMemberPermission>()
                .eq(TeamMemberPermission::getMemberId,userId));
    }

    @Override
    public Boolean deleteByTargetIds(Collection<Long> targetIds) {
        if (CollUtil.isEmpty(targetIds))return true;
        return baseMapper.delete(new LambdaQueryWrapper<TeamMemberPermission>()
                .in(TeamMemberPermission::getTarget, targetIds))>0;
    }


}
