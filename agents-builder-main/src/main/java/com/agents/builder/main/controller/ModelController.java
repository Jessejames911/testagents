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
import com.agents.builder.main.domain.vo.ModelVo;
import com.agents.builder.main.domain.bo.ModelBo;
import com.agents.builder.main.service.IModelService;
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
@RequestMapping("/model")
public class ModelController extends BaseController {

    private final IModelService modelService;

    /**
     * 查询列表
     */

    @GetMapping
    @SaCheckPermission(PermissionConstants.MODEL_READ)
    public R<List<ModelVo>> list(ModelBo bo) {
        return R.ok(modelService.queryList(bo));
    }




    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/{id}")
    @SaCheckPermission(PermissionConstants.MODEL_READ)
    public R<ModelVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(modelService.queryById(id));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    @SaCheckPermission(PermissionConstants.MODEL_CREATE)
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ModelBo bo) {
        return toAjax(modelService.insertByBo(bo));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{id}")
    @SaCheckPermission(PermissionConstants.MODEL_UPDATE)
    public R<Void> edit(@PathVariable Long id,@Validated(EditGroup.class) @RequestBody ModelBo bo) {
        bo.setId(id);
        return toAjax(modelService.updateByBo(bo));
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @SaCheckPermission(PermissionConstants.MODEL_DELETE)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long ids) {
        return toAjax(modelService.deleteWithValidByIds(List.of(ids), true));
    }
}
