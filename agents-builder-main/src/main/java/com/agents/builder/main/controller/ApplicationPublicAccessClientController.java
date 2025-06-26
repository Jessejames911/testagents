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
import com.agents.builder.main.domain.vo.ApplicationPublicAccessClientVo;
import com.agents.builder.main.domain.bo.ApplicationPublicAccessClientBo;
import com.agents.builder.main.service.IApplicationPublicAccessClientService;
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
@RequestMapping("/publicAccessClient")
public class ApplicationPublicAccessClientController extends BaseController {

    private final IApplicationPublicAccessClientService applicationPublicAccessClientService;

    /**
     * 查询列表
     */

    @GetMapping("/list")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public TableDataInfo<ApplicationPublicAccessClientVo> list(ApplicationPublicAccessClientBo bo, PageQuery pageQuery) {
        return applicationPublicAccessClientService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出列表
     */

    @Log(title = "", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ApplicationPublicAccessClientBo bo, HttpServletResponse response) {
        List<ApplicationPublicAccessClientVo> list = applicationPublicAccessClientService.queryList(bo);
        ExcelUtil.exportExcel(list, "", ApplicationPublicAccessClientVo.class, response);
    }

    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/{id}")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<ApplicationPublicAccessClientVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(applicationPublicAccessClientService.queryById(id));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ApplicationPublicAccessClientBo bo) {
        return toAjax(applicationPublicAccessClientService.insertByBo(bo));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ApplicationPublicAccessClientBo bo) {
        return toAjax(applicationPublicAccessClientService.updateByBo(bo));
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long ids) {
        return toAjax(applicationPublicAccessClientService.deleteWithValidByIds(List.of(ids), true));
    }
}
