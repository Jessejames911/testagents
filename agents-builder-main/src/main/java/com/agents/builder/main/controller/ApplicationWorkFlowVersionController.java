package com.agents.builder.main.controller;

import java.util.List;

import com.agents.builder.common.core.constant.PermissionConstants;
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
import com.agents.builder.main.domain.vo.ApplicationWorkFlowVersionVo;
import com.agents.builder.main.domain.bo.ApplicationWorkFlowVersionBo;
import com.agents.builder.main.service.IApplicationWorkFlowVersionService;
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
@RequestMapping("/application/{appId}/work_flow_version")
public class ApplicationWorkFlowVersionController extends BaseController {

    private final IApplicationWorkFlowVersionService applicationWorkFlowVersionService;

    /**
     * 查询列表
     */
    @GetMapping
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<List<ApplicationWorkFlowVersionVo>> list(@PathVariable Long appId, ApplicationWorkFlowVersionBo bo) {
        bo.setApplicationId(appId);
        return R.ok(applicationWorkFlowVersionService.queryList(bo));
    }



    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/{id}")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<ApplicationWorkFlowVersionVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(applicationWorkFlowVersionService.queryById(id));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ApplicationWorkFlowVersionBo bo) {
        return toAjax(applicationWorkFlowVersionService.insertByBo(bo));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{id}")
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<Void> edit(@PathVariable Long id,@RequestBody ApplicationWorkFlowVersionBo bo) {
        bo.setId(id);
        return toAjax(applicationWorkFlowVersionService.updateByBo(bo));
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @SaCheckPermission(PermissionConstants.APPLICATION_DELETE)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long ids) {
        return toAjax(applicationWorkFlowVersionService.deleteWithValidByIds(List.of(ids), true));
    }
}
