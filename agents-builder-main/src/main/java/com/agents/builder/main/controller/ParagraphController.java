package com.agents.builder.main.controller;

import java.util.List;

import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.common.core.enums.OperateTargetType;
import com.agents.builder.common.core.enums.OperateType;
import com.agents.builder.common.satoken.annotation.CheckTargetOperate;
import com.agents.builder.main.domain.bo.ProblemBo;
import com.agents.builder.main.domain.dto.DocGenerateRelatedDto;
import com.agents.builder.main.domain.vo.ProblemVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
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
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.domain.bo.ParagraphBo;
import com.agents.builder.main.service.IParagraphService;
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
@RequestMapping("/dataset/{datasetId}/document/{docId}/paragraph")
public class ParagraphController extends BaseController {

    private final IParagraphService paragraphService;


    /**
     * 查询列表
     */

    @GetMapping("/{pageNum}/{pageSize}")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public TableDataInfo<ParagraphVo> list(@PathVariable Integer pageNum,
                                           @PathVariable Integer pageSize,
                                           @PathVariable Long docId,
                                           ParagraphBo bo, PageQuery pageQuery) {
        pageQuery.setPageNum(pageNum);
        pageQuery.setPageSize(pageSize);
        bo.setDocumentId(docId);
        return paragraphService.queryPageList(bo, pageQuery);
    }



    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/{id}")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<ParagraphVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(paragraphService.queryById(id));
    }

    @GetMapping("/{id}/problem")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<List<ProblemVo>> getProblem(@NotNull(message = "主键不能为空")
                                  @PathVariable Long id) {
        return R.ok(paragraphService.getProblem(id));
    }

    @PostMapping("/{id}/problem")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> addProblem(@PathVariable Long id,
                                         @PathVariable Long datasetId,
                                         @PathVariable Long docId,
                                         @RequestBody ProblemBo bo) {
        bo.setDatasetId(datasetId);
        bo.setDocumentId(docId);
        bo.setParagraphId(id);
        bo.setId(IdWorker.getId());
        return toAjax(paragraphService.addProblem(bo));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> add(@PathVariable Long datasetId,
                       @PathVariable Long docId,
                       @Validated(AddGroup.class) @RequestBody ParagraphBo bo) {
        bo.setDatasetId(datasetId);
        bo.setDocumentId(docId);
        return toAjax(paragraphService.insertByBo(bo));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{id}")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> edit(@PathVariable Long id,
                        @PathVariable Long datasetId,
                        @PathVariable Long docId,
                        @Validated(EditGroup.class) @RequestBody ParagraphBo bo) {
        bo.setId(id);
        bo.setDocumentId(docId);
        bo.setDatasetId(datasetId);
        return toAjax(paragraphService.updateByBo(bo));
    }

    @RepeatSubmit()
    @PutMapping("/batch_generate_related")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> batchGenerateRelated(@PathVariable Long datasetId,
                                        @PathVariable Long docId,
                                        @RequestBody DocGenerateRelatedDto dto) {
        dto.setDocumentId(docId);
        dto.setDatasetId(datasetId);
        return toAjax(paragraphService.batchGenerateRelated(dto));
    }

    @RepeatSubmit()
    @PutMapping("/migrate/dataset/{targetDataset}/document/{targetDocument}")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> migrate(@PathVariable Long datasetId,
                           @PathVariable Long docId,
                           @PathVariable Long targetDataset,
                           @PathVariable Long targetDocument,
                           @RequestBody List<Long> paragraphIdList) {

        return toAjax(paragraphService.migrate(paragraphIdList,datasetId,targetDataset,docId,targetDocument));
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @SaCheckPermission(PermissionConstants.DATASET_DELETE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long ids,@PathVariable Long datasetId) {
        return toAjax(paragraphService.deleteWithValidByIds(List.of(ids), true));
    }
}
