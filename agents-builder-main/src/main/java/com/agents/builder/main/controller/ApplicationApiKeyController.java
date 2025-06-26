package com.agents.builder.main.controller;

import java.util.List;

import cn.hutool.core.lang.UUID;
import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.common.core.enums.OperateType;
import com.agents.builder.common.satoken.annotation.CheckTargetOperate;
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
import com.agents.builder.main.domain.vo.ApplicationApiKeyVo;
import com.agents.builder.main.domain.bo.ApplicationApiKeyBo;
import com.agents.builder.main.service.IApplicationApiKeyService;
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
@RequestMapping("/application")
public class ApplicationApiKeyController extends BaseController {

    private final IApplicationApiKeyService applicationApiKeyService;

    /**
     * 查询列表
     */

    @GetMapping("/{appId}/api_key")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#appId",operateType = OperateType.USE)
    public R<List<ApplicationApiKeyVo>> list(@PathVariable Long appId, ApplicationApiKeyBo bo) {
        return R.ok(applicationApiKeyService.queryList(bo));
    }



    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    @PostMapping("/{appId}/api_key")
    @CheckTargetOperate(value = "#appId",operateType = OperateType.MANAGE)
    public R<Void> add(@PathVariable Long appId) {
        ApplicationApiKeyBo bo = new ApplicationApiKeyBo();
        bo.setApplicationId(appId);
        bo.setIsActive(true);
        bo.setSecretKey(UUID.fastUUID().toString(true));
        bo.setAllowCrossDomain(false);
        return toAjax(applicationApiKeyService.insertByBo(bo));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    @PutMapping("/{appId}/api_key/{id}")
    @CheckTargetOperate(value = "#appId",operateType = OperateType.MANAGE)
    public R<Void> edit(@PathVariable Long appId,@PathVariable Long id,@RequestBody ApplicationApiKeyBo bo) {
        bo.setApplicationId(appId);
        bo.setId(id);
        return toAjax(applicationApiKeyService.updateByBo(bo));
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/api_key/{ids}")
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long ids) {
        return toAjax(applicationApiKeyService.deleteWithValidByIds(List.of(ids), true));
    }
}
