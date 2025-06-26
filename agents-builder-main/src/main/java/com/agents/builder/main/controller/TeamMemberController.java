package com.agents.builder.main.controller;

import java.util.List;
import java.util.Map;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.agents.builder.main.domain.vo.TeamMemberPermissionVo;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.agents.builder.common.web.annotation.RepeatSubmit;
import com.agents.builder.common.web.annotation.Log;
import com.agents.builder.common.web.core.BaseController;
import com.agents.builder.common.core.domain.R;
import com.agents.builder.common.web.enums.BusinessType;
import com.agents.builder.main.domain.vo.TeamMemberVo;
import com.agents.builder.main.domain.bo.TeamMemberBo;
import com.agents.builder.main.service.ITeamMemberService;

/**
 *
 *
 * @author Angus
 * @date 2024-10-31
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/team/member")
public class TeamMemberController extends BaseController {

    private final ITeamMemberService teamMemberService;


    @GetMapping
    @SaCheckPermission(PermissionConstants.TEAM_READ)
    public R<List<TeamMemberVo>> getMember() {
        return R.ok(teamMemberService.getUserTeamMembers(LoginHelper.getUserId()));
    }

    @PostMapping("/_batch")
    @SaCheckPermission(PermissionConstants.TEAM_UPDATE)
    public R<Void> batchSaveMembers(@RequestBody List<Long> userIdList) {
        Long userId = LoginHelper.getUserId();
        userIdList.remove(userId);

        return toAjax(teamMemberService.batchSaveMembers(userIdList,LoginHelper.getUserId()));
    }



    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/root")
    @SaCheckPermission(PermissionConstants.TEAM_READ)
    public R<Map<String, List<TeamMemberPermissionVo>>> getRootInfos() {
        return R.ok(teamMemberService.getRootInfos(LoginHelper.getUserId()));
    }

    @GetMapping("/{userId}")
    @SaCheckPermission(PermissionConstants.TEAM_READ)
    public R<Map<String, List<TeamMemberPermissionVo>>> getUserInfos(@NotNull(message = "主键不能为空")
                                                                     @PathVariable Long userId) {

        return R.ok(teamMemberService.getUserInfos(userId));
    }

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{id}")
    @SaCheckPermission(PermissionConstants.TEAM_UPDATE)
    public R<Void> edit(@PathVariable Long id, @RequestBody TeamMemberBo bo) {
        bo.setId(id);
        return toAjax(teamMemberService.updatePersmissionByBo(bo));
    }



    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    @SaCheckPermission(PermissionConstants.TEAM_UPDATE)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long id) {
        return toAjax(teamMemberService.remove(id));
    }
}
