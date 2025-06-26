package com.agents.builder.main.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.agents.builder.common.ai.enums.ModelType;
import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.common.core.domain.R;
import com.agents.builder.common.core.enums.OperateType;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.satoken.annotation.CheckTargetOperate;
import com.agents.builder.common.web.annotation.Log;
import com.agents.builder.common.web.annotation.RepeatSubmit;
import com.agents.builder.common.web.core.BaseController;
import com.agents.builder.common.web.enums.BusinessType;
import com.agents.builder.main.domain.dto.EmbedDto;
import com.agents.builder.main.domain.bo.*;
import com.agents.builder.main.domain.dto.ChatMessageDto;
import com.agents.builder.main.domain.dto.SearchDto;
import com.agents.builder.main.domain.vo.*;
import com.agents.builder.main.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * 应用接口
 * @author Angus
 * @date 2024-10-31
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/application")
public class ApplicationController extends BaseController {

    private final IApplicationService applicationService;

    private final IDatasetService datasetService;

    private final IModelService modelService;

    private final IFunctionLibService functionLibService;

    private final IApplicationAccessTokenService applicationAccessTokenService;

    /**
     * 对话
     *
     * @param chatId chat id
     * @param dto    dto
     * @return {@link Flux }<{@link ServerSentEvent }<{@link ChatMessageVo }>>
     */
    @PostMapping(value = "/chat_message/{chatId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public Flux<ServerSentEvent<ChatMessageVo>> streamChat(@PathVariable Long chatId, @RequestBody ChatMessageDto dto) {
        dto.setChatId(chatId);
        return applicationService.streamChat(dto) .map(res->  ServerSentEvent.<ChatMessageVo>builder()
                                .data(res)
                                .build()
                )
                //发生异常时发送空对象
                .onErrorResume(e -> Flux.empty());
    }



    /**
     * 查询列表
     */
    @GetMapping
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<List<ApplicationVo>> list(ApplicationBo bo) {
        return R.ok(applicationService.queryList(bo));
    }



    @GetMapping("/{appId}/statistics/chat_record_aggregate_trend")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#appId",operateType = OperateType.USE)
    public R<?> statisticsChatRecordAggregate(@PathVariable Long appId) {
        // todo 统计信息
        return R.ok(Collections.emptyList());
    }

    @GetMapping("/{pageNum}/{pageSize}")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public TableDataInfo<ApplicationVo> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                             ApplicationBo bo, PageQuery pageQuery) {
        pageQuery.setPageNum(pageNum);
        pageQuery.setPageSize(pageSize);
        return applicationService.queryPageList(bo, pageQuery);
    }


    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/{id}")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#id",operateType = OperateType.USE)
    public R<ApplicationVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(applicationService.queryById(id));
    }

    @GetMapping("/{id}/setting")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#id",operateType = OperateType.USE)
    public R<?> getSetting(@NotNull(message = "主键不能为空")
                                    @PathVariable Long id) {
        ApplicationAccessTokenVo accessTokenVo = applicationAccessTokenService.queryById(id);
        accessTokenVo.setAvatar(accessTokenVo.getAvatarUrl());
        accessTokenVo.setUserAvatar(accessTokenVo.getUserAvatarUrl());
        accessTokenVo.setFloatIcon(accessTokenVo.getFloatIconUrl());
        return R.ok(accessTokenVo);
    }

    @PutMapping("/{id}/setting")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#id",operateType = OperateType.USE)
    public R<?> updateSetting(@NotNull(message = "主键不能为空")
                           @PathVariable Long id,ApplicationAccessTokenBo bo) {
        bo.setApplicationId(id);
        return R.ok(applicationAccessTokenService.updateByBo(bo));
    }

    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/profile")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<ApplicationVo> getProfile() {
        return R.ok(applicationService.getProfile());
    }


    /**
     * 嵌入第三方
     *
     * @param dto      dto
     * @param response response
     */
    @GetMapping("/embed")
    @SaIgnore
    public void embed(EmbedDto dto, HttpServletResponse response) {
        applicationService.embed(dto,response);
    }



    @GetMapping("/{id}/hit_test")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#id",operateType = OperateType.USE)
    public R<List<ParagraphVo>> hitTest(@PathVariable Long id, SearchDto dto) {
        dto.setApplication_id(id);
        return R.ok(applicationService.hitTest(dto));
    }



    @GetMapping("/{appId}/list_dataset")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#appId",operateType = OperateType.USE)
    public R<List<DatasetVo>> getDataset(@PathVariable Long appId) {
        return R.ok(datasetService.queryList(new DatasetBo()));
    }

    @GetMapping("/{appId}/application")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#appId",operateType = OperateType.USE)
    public R<List<ApplicationVo>> getAvailableAppList(@PathVariable Long appId) {
        return R.ok(applicationService.queryList(new ApplicationBo()).stream().filter(item->!appId.equals(item.getId())).collect(Collectors.toList()));
    }

    @GetMapping("/{appId}/model")
    @CheckTargetOperate(value = "#appId",operateType = OperateType.USE)
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<List<ModelVo>> getModel(@PathVariable Long appId) {
        ModelBo modelBo = new ModelBo();
        modelBo.setModel_type(ModelType.LLM.getKey());
        return R.ok(modelService.queryList(modelBo));
    }

    @GetMapping("/{appId}/function_lib")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#appId",operateType = OperateType.USE)
    public R<List<FunctionLibVo>> getFunctionLib(@PathVariable Long appId) {
        return R.ok(functionLibService.queryList(new FunctionLibBo()));
    }

    @GetMapping("/{appId}/function_lib/{functionLibId}")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#appId",operateType = OperateType.USE)
    public R<FunctionLibVo> getFunctionLibById(@PathVariable Long appId,@PathVariable Long functionLibId) {
        return R.ok(functionLibService.queryById(functionLibId));
    }


    @GetMapping("/{appId}/model_params_form/{modelId}")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    @CheckTargetOperate(value = "#appId",operateType = OperateType.USE)
    public R<List<ModelFormVo>> getModelParamsForm(@PathVariable Long appId,@PathVariable Long modelId) {
        return R.ok(modelService.getModelParamsForm(modelId));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    @SaCheckPermission(PermissionConstants.APPLICATION_CREATE)
    public R<ApplicationVo> add(@Validated(AddGroup.class) @RequestBody ApplicationBo bo) {
        return R.ok(applicationService.insertByBo(bo));
    }


    @PostMapping("/authentication")
    @SaIgnore
    public R<?> auth(@RequestBody ApplicationAccessTokenBo bo){
        return R.ok("认证成功",applicationService.auth(bo.getAccessToken()));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{id}")
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    @CheckTargetOperate(value = "#id",operateType = OperateType.MANAGE)
    public R<Void> edit(@PathVariable Long id,@Validated(EditGroup.class) @RequestBody ApplicationBo bo) {
        bo.setId(id);
        return toAjax(applicationService.updateByBo(bo));
    }

    @RepeatSubmit()
    @PutMapping("/{id}/publish")
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    @CheckTargetOperate(value = "#id",operateType = OperateType.MANAGE)
    public R<Void> publish(@PathVariable Long id,@Validated(EditGroup.class) @RequestBody ApplicationBo bo) {
        return toAjax(applicationService.publish(id,bo.getWorkFlow()));
    }


    @Log(title = "", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/edit_icon")
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    @CheckTargetOperate(value = "#id",operateType = OperateType.MANAGE)
    public R<Void> editIcon(@PathVariable Long id, ApplicationBo bo) {
        return toAjax(applicationService.editIcon(id,bo.getFile()));
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @SaCheckPermission(PermissionConstants.APPLICATION_DELETE)
    @CheckTargetOperate(value = "#ids",operateType = OperateType.MANAGE)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long ids) {
        return toAjax(applicationService.deleteWithValidByIds(List.of(ids), true));
    }
}
