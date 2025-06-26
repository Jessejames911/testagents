package com.agents.builder.main.controller;

import java.util.List;

import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.agents.builder.main.domain.vo.UserVo;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.agents.builder.common.web.annotation.RepeatSubmit;
import com.agents.builder.common.web.annotation.Log;
import com.agents.builder.common.web.core.BaseController;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.core.domain.R;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.agents.builder.common.web.enums.BusinessType;
import com.agents.builder.common.core.excel.utils.ExcelUtil;
import com.agents.builder.main.domain.vo.TeamVo;
import com.agents.builder.main.domain.bo.TeamBo;
import com.agents.builder.main.service.ITeamService;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;

/**
 *
 *
 * @author Angus
 * @date 2024-10-31
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/team")
public class TeamController extends BaseController {

    private final ITeamService teamService;

    /**
     * 查询列表
     */

    @GetMapping("/list")
    @SaCheckPermission(PermissionConstants.TEAM_READ)
    public TableDataInfo<TeamVo> list(TeamBo bo, PageQuery pageQuery) {
        return teamService.queryPageList(bo, pageQuery);
    }



    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/{id}")
    @SaCheckPermission(PermissionConstants.TEAM_READ)
    public R<TeamVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(teamService.queryById(id));
    }



    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    @SaCheckPermission(PermissionConstants.TEAM_CREATE)
    public R<Void> add(@Validated(AddGroup.class) @RequestBody TeamBo bo) {
        return toAjax(teamService.insertByBo(bo));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    @SaCheckPermission(PermissionConstants.TEAM_UPDATE)
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody TeamBo bo) {
        return toAjax(teamService.updateByBo(bo));
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @SaCheckPermission(PermissionConstants.TEAM_DELETE)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long ids) {
        return toAjax(teamService.deleteWithValidByIds(List.of(ids), true));
    }
}
