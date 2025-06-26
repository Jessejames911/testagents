package com.agents.builder.main.controller;

import java.util.Collections;
import java.util.List;

import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.main.domain.dto.FunctionDebugDto;
import com.agents.builder.main.domain.vo.FunctionDebugVo;
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
import com.agents.builder.main.domain.vo.FunctionLibVo;
import com.agents.builder.main.domain.bo.FunctionLibBo;
import com.agents.builder.main.service.IFunctionLibService;
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
@RequestMapping("/function_lib")
public class FunctionLibController extends BaseController {

    private final IFunctionLibService functionLibService;

    /**
     * 查询列表
     */

    @GetMapping("/{pageNum}/{pageSize}")
    @SaCheckPermission(PermissionConstants.FUNCTION_LIB_READ)
    public TableDataInfo<FunctionLibVo> list(@PathVariable Integer pageNum,
                                             @PathVariable Integer pageSize,
                                             FunctionLibBo bo,
                                             PageQuery pageQuery) {
        pageQuery.setPageNum(pageNum);
        pageQuery.setPageSize(pageSize);
        return functionLibService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取详细信息
     *
     * @param 主键
     */
    @GetMapping("/{id}")
    @SaCheckPermission(PermissionConstants.FUNCTION_LIB_READ)
    public R<FunctionLibVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(functionLibService.queryById(id));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    @SaCheckPermission(PermissionConstants.FUNCTION_LIB_CREATE)
    public R<FunctionLibVo> add(@Validated(AddGroup.class) @RequestBody FunctionLibBo bo) {
        return R.ok(functionLibService.insertByBo(bo));
    }

    @PostMapping("/debug")
    @SaCheckPermission(PermissionConstants.FUNCTION_LIB_READ)
    public R<?> debug(@Validated @RequestBody FunctionDebugDto dto) {
        return R.ok(functionLibService.debug(dto));
    }

    @PostMapping("/pylint")
    @SaCheckPermission(PermissionConstants.FUNCTION_LIB_READ)
    public R<?> pylint(@RequestBody FunctionDebugDto dto) {
        return R.ok(Collections.emptyList());
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{id}")
    @SaCheckPermission(PermissionConstants.FUNCTION_LIB_UPDATE)
    public R<Void> edit(@PathVariable Long id,@Validated(EditGroup.class) @RequestBody FunctionLibBo bo) {
        bo.setId(id);
        return toAjax(functionLibService.updateByBo(bo));
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @SaCheckPermission(PermissionConstants.FUNCTION_LIB_DELETE)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long ids) {
        return toAjax(functionLibService.deleteWithValidByIds(List.of(ids), true));
    }
}
