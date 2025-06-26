package com.agents.builder.main.controller;

import java.util.List;

import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.main.domain.dto.DocImproveDto;
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
import com.agents.builder.main.domain.vo.ApplicationChatRecordVo;
import com.agents.builder.main.domain.bo.ApplicationChatRecordBo;
import com.agents.builder.main.service.IApplicationChatRecordService;
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
@RequestMapping("/application/{appId}/chat/{chatId}")
public class ApplicationChatRecordController extends BaseController {

    private final IApplicationChatRecordService applicationChatRecordService;

    /**
     * 查询列表
     */

    @GetMapping("/chat_record/{pageNum}/{pageSize}")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public TableDataInfo<ApplicationChatRecordVo> list(@PathVariable Integer pageNum,
                                                       @PathVariable Integer pageSize,
                                                       @PathVariable Long chatId,
                                                       ApplicationChatRecordBo bo, PageQuery pageQuery) {
        pageQuery.setPageNum(pageNum);
        pageQuery.setPageSize(pageSize);
        bo.setChatId(chatId);
        return applicationChatRecordService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/chat_record/{id}")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<ApplicationChatRecordVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id,
                                              @PathVariable Long chatId) {
        return R.ok(applicationChatRecordService.getDetail(id,chatId));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/chat_record")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ApplicationChatRecordBo bo) {
        return toAjax(applicationChatRecordService.insertByBo(bo));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/chat_record/{recordId}")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<Void> edit(@PathVariable Long recordId, @Validated(EditGroup.class) @RequestBody ApplicationChatRecordBo bo) {
        bo.setId(recordId);
        return toAjax(applicationChatRecordService.updateByBo(bo));
    }

    @RepeatSubmit()
    @PutMapping("/chat_record/{recordId}/dataset/{datasetId}/document_id/{docId}/improve")
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<ApplicationChatRecordVo> improveDoc(@PathVariable Long recordId,
                              @PathVariable Long docId,
                              @PathVariable Long datasetId,
                              @RequestBody DocImproveDto dto) {
        dto.setRecordId(recordId);
        dto.setDatasetId(datasetId);
        dto.setDocumentId(docId);
        return R.ok(applicationChatRecordService.improveDoc(dto));
    }


    @RepeatSubmit()
    @PutMapping("/chat_record/{recordId}/vote")
    @SaCheckPermission(PermissionConstants.APPLICATION_READ)
    public R<Void> vote(@PathVariable Long recordId,@RequestBody ApplicationChatRecordBo bo) {
        bo.setId(recordId);
        return toAjax(applicationChatRecordService.updateByBo(bo));
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/chat_record/{ids}")
    @SaCheckPermission(PermissionConstants.APPLICATION_UPDATE)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long ids) {
        return toAjax(applicationChatRecordService.deleteWithValidByIds(List.of(ids), true));
    }
}
