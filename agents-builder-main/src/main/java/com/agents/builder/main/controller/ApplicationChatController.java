package com.agents.builder.main.controller;

import java.util.List;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.common.core.enums.OperateType;
import com.agents.builder.common.satoken.annotation.CheckTargetOperate;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.agents.builder.main.domain.bo.ApplicationBo;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.*;
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
import com.agents.builder.main.domain.vo.ApplicationChatVo;
import com.agents.builder.main.domain.bo.ApplicationChatBo;
import com.agents.builder.main.service.IApplicationChatService;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;

/**
 * 会话接口
 *
 * @author Angus
 * @date 2024-10-31
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/application")
public class ApplicationChatController extends BaseController {

    private final IApplicationChatService applicationChatService;

    /**
     * 开启临时会话
     *
     * @param bo bo
     * @return {@link R }<{@link String }>
     */
    @PostMapping("/chat/open")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#bo.id",operateType = OperateType.USE)
    public R<String> tempOpen(@RequestBody ApplicationBo bo){
        return R.ok("成功",applicationChatService.tempOpen(bo));
    }

    @PostMapping("/chat_workflow/open")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<String> tempWorkflowOpen(@RequestBody ApplicationBo bo){
        return R.ok("成功",applicationChatService.tempWorkflowOpen(bo.getWorkFlow()));
    }


    /**
     * 用户端开启会话
     *
     * @param appId app id
     * @return {@link R }<{@link String }>
     */
    @GetMapping("/{appId}/chat/open")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#appId",operateType = OperateType.USE)
    public R<String> clientOpen(@PathVariable Long appId){
        return R.ok("成功",applicationChatService.clientOpen(appId));
    }

    /**
     * 查询列表
     */
    @GetMapping("/{appId}/chat/{pageNum}/{pageSize}")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#appId",operateType = OperateType.USE)
    public TableDataInfo<ApplicationChatVo> list(@PathVariable Integer pageNum,
                                                 @PathVariable Integer pageSize,
                                                 @PathVariable Long appId,
                                                 ApplicationChatBo bo, PageQuery pageQuery) {
        pageQuery.setPageNum(pageNum);
        pageQuery.setPageSize(pageSize);
        bo.setApplicationId(appId);
        return applicationChatService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取用户端会话列表
     *
     * @param pageNum   page num
     * @param pageSize  page size
     * @param appId     app id
     * @param bo        bo
     * @param pageQuery page query
     * @return {@link TableDataInfo }<{@link ApplicationChatVo }>
     */
    @GetMapping("/{appId}/chat/client/{pageNum}/{pageSize}")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public TableDataInfo<ApplicationChatVo> getClientList(@PathVariable Integer pageNum,
                                                 @PathVariable Integer pageSize,
                                                 @PathVariable Long appId,
                                                 ApplicationChatBo bo, PageQuery pageQuery) {
        pageQuery.setPageNum(pageNum);
        pageQuery.setPageSize(pageSize);
        bo.setApplicationId(appId);
        bo.setClientId(LoginHelper.getLoginUser().getClientId());
        return applicationChatService.queryPageList(bo, pageQuery);
    }


    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/chat/{id}")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<ApplicationChatVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(applicationChatService.queryById(id));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/chat")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ApplicationChatBo bo) {
        return toAjax(applicationChatService.insertByBo(bo));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/chat")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ApplicationChatBo bo) {
        return toAjax(applicationChatService.updateByBo(bo));
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/chat/{ids}")
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(applicationChatService.deleteWithValidByIds(List.of(ids), true));
    }

    @DeleteMapping("{appId}/chat/client/{ids}")
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<Void> removeClient(@NotNull(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(applicationChatService.deleteWithValidByIds(List.of(ids), true));
    }
}
