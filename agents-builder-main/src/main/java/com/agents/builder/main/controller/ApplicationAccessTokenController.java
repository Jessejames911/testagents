package com.agents.builder.main.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.UUID;
import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.common.core.domain.R;
import com.agents.builder.common.core.enums.OperateType;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.satoken.annotation.CheckTargetOperate;
import com.agents.builder.common.web.annotation.Log;
import com.agents.builder.common.web.annotation.RepeatSubmit;
import com.agents.builder.common.web.core.BaseController;
import com.agents.builder.common.web.enums.BusinessType;
import com.agents.builder.main.domain.bo.ApplicationAccessTokenBo;
import com.agents.builder.main.domain.vo.ApplicationAccessTokenVo;
import com.agents.builder.main.service.IApplicationAccessTokenService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
public class ApplicationAccessTokenController extends BaseController {

    private final IApplicationAccessTokenService applicationAccessTokenService;

    /**
     * 查询列表
     */
    @GetMapping("/access_token/list")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public TableDataInfo<ApplicationAccessTokenVo> list(ApplicationAccessTokenBo bo, PageQuery pageQuery) {
        return applicationAccessTokenService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/{appId}/access_token")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#appId",operateType = OperateType.USE)
    public R<ApplicationAccessTokenVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long appId) {
        return R.ok(applicationAccessTokenService.queryById(appId));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/access_token")
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ApplicationAccessTokenBo bo) {
        return toAjax(applicationAccessTokenService.insertByBo(bo));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{id}/access_token")
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<?> edit(@PathVariable Long id, @RequestBody ApplicationAccessTokenBo bo) {
        bo.setApplicationId(id);
        if (bo.getAccessTokenReset()){
            bo.setAccessToken(UUID.fastUUID().toString(true));
        }
        applicationAccessTokenService.updateByBo(bo);
        return R.ok(bo);
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/access_token/{ids}")
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long ids) {
        return toAjax(applicationAccessTokenService.deleteWithValidByIds(List.of(ids), true));
    }
}
