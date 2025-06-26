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
import com.agents.builder.main.domain.vo.ApplicationDatasetMappingVo;
import com.agents.builder.main.domain.bo.ApplicationDatasetMappingBo;
import com.agents.builder.main.service.IApplicationDatasetMappingService;
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
@RequestMapping("/datasetMapping")
public class ApplicationDatasetMappingController extends BaseController {

    private final IApplicationDatasetMappingService applicationDatasetMappingService;

    /**
     * 查询列表
     */

    @GetMapping("/list")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public TableDataInfo<ApplicationDatasetMappingVo> list(ApplicationDatasetMappingBo bo, PageQuery pageQuery) {
        return applicationDatasetMappingService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出列表
     */

    @Log(title = "", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ApplicationDatasetMappingBo bo, HttpServletResponse response) {
        List<ApplicationDatasetMappingVo> list = applicationDatasetMappingService.queryList(bo);
        ExcelUtil.exportExcel(list, "", ApplicationDatasetMappingVo.class, response);
    }

    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/{id}")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<ApplicationDatasetMappingVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(applicationDatasetMappingService.queryById(id));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ApplicationDatasetMappingBo bo) {
        return toAjax(applicationDatasetMappingService.insertByBo(bo));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ApplicationDatasetMappingBo bo) {
        return toAjax(applicationDatasetMappingService.updateByBo(bo));
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
        return toAjax(applicationDatasetMappingService.deleteWithValidByIds(List.of(ids), true));
    }
}
