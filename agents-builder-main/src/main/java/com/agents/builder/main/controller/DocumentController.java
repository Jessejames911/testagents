package com.agents.builder.main.controller;

import java.util.List;

import com.agents.builder.common.core.config.properties.SystemConfigResourceProperties;
import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.common.core.domain.model.KVObj;
import com.agents.builder.common.core.enums.OperateTargetType;
import com.agents.builder.common.core.enums.OperateType;
import com.agents.builder.common.core.utils.file.FileUtils;
import com.agents.builder.common.satoken.annotation.CheckTargetOperate;
import com.agents.builder.main.domain.dto.DocGenerateRelatedDto;
import com.agents.builder.main.domain.dto.SplitDto;
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
import com.agents.builder.common.web.enums.BusinessType;
import com.agents.builder.main.domain.vo.DocumentVo;
import com.agents.builder.main.domain.bo.DocumentBo;
import com.agents.builder.main.service.IDocumentService;
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
@RequestMapping("/dataset")
public class DocumentController extends BaseController {

    private final IDocumentService documentService;

    private final SystemConfigResourceProperties systemConfigResourceProperties;

    /**
     * 查询列表
     */
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    @GetMapping("/{datasetId}/document/{pageNum}/{pageSize}")
    public TableDataInfo<DocumentVo> list(@PathVariable Integer pageNum,
                                          @PathVariable Integer pageSize,
                                          @PathVariable Long datasetId,
                                          DocumentBo bo, PageQuery pageQuery) {
        pageQuery.setPageNum(pageNum);
        pageQuery.setPageSize(pageSize);
        bo.setDatasetId(datasetId);
        return documentService.queryPageList(bo, pageQuery);
    }


    @GetMapping("/{datasetId}/document")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<List<DocumentVo>> getAll(@PathVariable Long datasetId, DocumentBo bo) {
        bo.setDatasetId(datasetId);
        return R.ok(documentService.queryList(bo));
    }

    @PostMapping("/document/split")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<List<DocumentVo>> split(SplitDto dto) {
        return R.ok(documentService.split(dto));
    }


    @PostMapping("/{datasetId}/document/table")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<List<DocumentVo>> table(@PathVariable Long datasetId,SplitDto dto) {
        dto.setDatasetId(datasetId);
        return R.ok(documentService.importTable(dto));
    }

    @PostMapping("/{datasetId}/document/qa")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<List<DocumentVo>> qa(@PathVariable Long datasetId,SplitDto dto) {
        dto.setDatasetId(datasetId);
        return R.ok(documentService.importQa(dto));
    }

    @GetMapping("/document/split_pattern")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<List<KVObj>>getSplitPattern() {
        return R.ok(documentService.getSplitPattern());
    }

    @GetMapping("/document/template/export")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public void qaTemplateExport(@RequestParam(required = false,defaultValue = "excel") String type,HttpServletResponse response) {
        if ("csv".equals(type)){
            FileUtils.exportExcel(response, systemConfigResourceProperties.getQaCsvTemplatePath(),"qa_template.csv","text/cxv");
        }else {
            FileUtils.exportExcel(response, systemConfigResourceProperties.getQaExcelTemplatePath(),"qa_template.xlsx","application/vnd.ms-excel");
        }
    }

    @GetMapping("/document/table_template/export")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public void tableTemplateExport(@RequestParam(required = false,defaultValue = "excel") String type,HttpServletResponse response) {
        if ("csv".equals(type)){
            FileUtils.exportExcel(response, systemConfigResourceProperties.getTableCsvTemplatePath(),"table_template.csv","text/cxv");
        }else {
            FileUtils.exportExcel(response, systemConfigResourceProperties.getTableExcelTemplatePath(),"table_template.xlsx","application/vnd.ms-excel");
        }
    }


    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/{datasetId}/document/{id}")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<DocumentVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(documentService.queryById(id));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/{datasetId}/document/_bach")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> add(@PathVariable Long datasetId, @RequestBody List<DocumentBo> boList) {
        boList.forEach(bo -> bo.setDatasetId(datasetId));
        return toAjax(documentService.insertBatchByBo(boList));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{datasetId}/document/{id}")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<DocumentVo> edit(@PathVariable Long datasetId,@PathVariable Long id, @RequestBody DocumentBo bo) {
        bo.setId(id);
        return R.ok(documentService.updateByBo(bo));
    }

    @RepeatSubmit(interval = 1000 * 60, message = "操作过于频繁")
    @PutMapping("/{datasetId}/document/{id}/refresh")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> refresh(@PathVariable Long id,@PathVariable Long datasetId) {
        return toAjax(documentService.refresh(id,datasetId));
    }

    @RepeatSubmit(interval = 1000 * 60 * 2, message = "操作过于频繁")
    @PutMapping("/{datasetId}/document/batch_refresh")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> batchRefresh(@PathVariable Long datasetId,@RequestBody DocumentBo bo) {
        return toAjax(documentService.batchRefresh(bo.getIdList(),datasetId));
    }

    @RepeatSubmit()
    @PutMapping("/{datasetId}/document/batch_hit_handling")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> batchHitHandling(@PathVariable Long datasetId,@RequestBody DocumentBo bo) {
        return toAjax(documentService.batchHitHandling(bo));
    }

    @RepeatSubmit()
    @PutMapping("/{datasetId}/document/batch_generate_related")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> batchGenerateRelated(@PathVariable Long datasetId,@RequestBody DocGenerateRelatedDto dto) {
        dto.setDatasetId(datasetId);
        return toAjax(documentService.batchGenerateRelated(dto));
    }

    @RepeatSubmit(interval = 1000 * 60, message = "操作过于频繁")
    @PutMapping("/{datasetId}/document/migrate/{target}")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> migrate(@PathVariable Long datasetId,
                                @PathVariable Long target,
                                @RequestBody List<Long> documentIdList) {
        return toAjax(documentService.migrate(documentIdList,datasetId,target));
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{datasetId}/document/{id}")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long id,@PathVariable Long datasetId,DocumentBo bo) {
        return toAjax(documentService.deleteWithValidByIds(List.of(id), true));
    }

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{datasetId}/document/_bach")
    @SaCheckPermission(PermissionConstants.DATASET_DELETE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> batchRemove(@PathVariable Long datasetId,@RequestBody DocumentBo bo) {
        return toAjax(documentService.deleteWithValidByIds(bo.getIdList(), true));
    }
}
