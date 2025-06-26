package com.agents.builder.main.controller;

import java.util.List;
import java.util.Map;

import cn.dev33.satoken.annotation.SaIgnore;
import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.common.core.enums.SettingType;
import com.agents.builder.main.domain.dto.DisplaySettingsDto;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.agents.builder.common.web.annotation.RepeatSubmit;
import com.agents.builder.common.web.annotation.Log;
import com.agents.builder.common.web.core.BaseController;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.core.domain.R;
import com.agents.builder.common.web.enums.BusinessType;
import com.agents.builder.main.domain.vo.SystemSettingVo;
import com.agents.builder.main.domain.bo.SystemSettingBo;
import com.agents.builder.main.service.ISystemSettingService;
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
@RequestMapping()
public class SystemSettingController extends BaseController {

    private final ISystemSettingService systemSettingService;




    /**
     * 查询列表
     */

    @GetMapping("/setting/list")
    @SaCheckPermission(PermissionConstants.SETTING_READ)
    public TableDataInfo<SystemSettingVo> list(SystemSettingBo bo, PageQuery pageQuery) {
        return systemSettingService.queryPageList(bo, pageQuery);
    }



    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/setting/{id}")
    @SaCheckPermission(PermissionConstants.SETTING_READ)
    public R<SystemSettingVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(systemSettingService.queryById(id));
    }


    @GetMapping("/email_setting")
    @SaCheckPermission(PermissionConstants.SETTING_READ)
    public R<?> getEmailSetting() {
        SystemSettingVo settingVo = systemSettingService.getByType(SettingType.EMAIL);
        return R.ok(settingVo==null?null:settingVo.getMeta());
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PutMapping("/email_setting")
    @SaCheckPermission(PermissionConstants.SETTING_CREATE)
    public R<Void> saveOrUpdateEmailSetting(@RequestBody Map<String, Object> emailSetting) {

        return toAjax(systemSettingService.saveOrUpdateEmailSetting(emailSetting));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/email_setting")
    @SaCheckPermission(PermissionConstants.SETTING_UPDATE)
    public R<Void> testEmailSetting(@RequestBody Map<String, Object> emailSetting) {
        return toAjax(systemSettingService.emailTest(emailSetting));
    }


    @Log(title = "", businessType = BusinessType.UPDATE)
    @PostMapping("/display/update")
    @SaCheckPermission(PermissionConstants.SETTING_UPDATE)
    public R<Void> saveOrUpdateDisplaySetting(DisplaySettingsDto dto) {
        return toAjax(systemSettingService.saveOrUpdateDisplaySetting(dto));
    }

    @GetMapping("/display/info")
    @SaIgnore
    @SaCheckPermission(PermissionConstants.SETTING_READ)
    public R<?> getDisplayInfo() {
        SystemSettingVo settingVo = systemSettingService.getByType(SettingType.DISPLAY);
        return R.ok(settingVo==null?null:settingVo.getMeta());
    }

    @GetMapping("/auth/types")
    @SaIgnore
    @SaCheckPermission(PermissionConstants.SETTING_READ)
    public R<?> getAuthTypes() {
        return R.ok();
    }

    @GetMapping("/qr_type")
    @SaIgnore
    @SaCheckPermission(PermissionConstants.SETTING_READ)
    public R<?> getQrType() {
        return R.ok();
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/setting/{ids}")
    @SaCheckPermission(PermissionConstants.SETTING_DELETE)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long ids) {
        return toAjax(systemSettingService.deleteWithValidByIds(List.of(ids), true));
    }
}
