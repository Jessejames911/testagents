package com.agents.builder.main.service.impl;

import cn.hutool.core.convert.Convert;
import com.agents.builder.common.core.enums.OperateTargetType;
import com.agents.builder.common.core.enums.OperateType;
import com.agents.builder.common.core.utils.StreamUtils;
import com.agents.builder.main.domain.vo.TeamMemberPermissionVo;
import com.agents.builder.main.mapper.TeamMemberPermissionMapper;
import com.agents.builder.common.core.service.DataScopeService;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据权限 实现
 * <p>
 * 注意: 此Service内不允许调用标注`数据权限`注解的方法
 * 例如: deptMapper.selectList 此 selectList 方法标注了`数据权限`注解 会出现循环解析的问题
 *
 * @author Angus
 */
@RequiredArgsConstructor
@Service("sdss")
public class DataScopeServiceImpl implements DataScopeService {

    private final TeamMemberPermissionMapper permissionMapper;

    private final static Set<String> MAIN = Set.of(OperateTargetType.APP.getKey(), OperateTargetType.DATASET.getKey());

    private final static Set<String> BASE = Set.of(OperateTargetType.MODEL.getKey(), OperateTargetType.FUNCTION_LIB.getKey());


    @Override
    public String getUserCustom(Long userId, String targetType, boolean isSelect) {
        List<TeamMemberPermissionVo> permissions = new ArrayList<>();

        if (MAIN.contains(targetType)) {
            List<TeamMemberPermissionVo> userMainTargetPermission = permissionMapper.getUserMainTargetPermission(userId, targetType);
            permissions = userMainTargetPermission.stream().map(m->{
                m.setOperate(JSONObject.parseObject(m.getOperateStr(), Map.class));
                return m;
                    }).filter(item -> (item.getOperate() != null && item.getOperate().get(OperateType.USE.getKey()) && isSelect)
                            || (item.getOperate() != null && item.getOperate().get(OperateType.MANAGE.getKey()) && isSelect)
                            || (item.getOperate() != null && item.getOperate().get(OperateType.MANAGE.getKey()) && !isSelect))
                    .collect(Collectors.toList());
        }

        if (BASE.contains(targetType)) {
            List<TeamMemberPermissionVo> teamMemberPermissionVos = permissionMapper.getUserBaseTargetPermission(userId, targetType);
            if (isSelect){
                permissions = teamMemberPermissionVos;
            }else {
                // 只容许操作自己的数据
                teamMemberPermissionVos.stream().filter(item->userId.equals(item.getUserId())).collect(Collectors.toList());
            }
        }
        TeamMemberPermissionVo teamMemberPermission = new TeamMemberPermissionVo();
        teamMemberPermission.setId(-1L);
        permissions.add(teamMemberPermission);
        return StreamUtils.join(permissions, rd -> Convert.toStr(rd.getId()));
    }

    @Override
    public Set<String> getAllPermissions(Long userId) {
        return null;
    }
}
