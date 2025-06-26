package com.agents.builder.main.controller;

import java.util.List;

import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.common.core.enums.OperateTargetType;
import com.agents.builder.common.core.enums.OperateType;
import com.agents.builder.common.satoken.annotation.CheckTargetOperate;
import com.agents.builder.main.domain.bo.ProblemBo;
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
import com.agents.builder.main.domain.vo.ProblemParagraphMappingVo;
import com.agents.builder.main.domain.bo.ProblemParagraphMappingBo;
import com.agents.builder.main.service.IProblemParagraphMappingService;
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
@RequestMapping("/dataset/{datasetId}/document/{docId}/paragraph/{paragraphId}/problem/{problemId}")
public class ProblemParagraphMappingController extends BaseController {

    private final IProblemParagraphMappingService problemParagraphMappingService;


    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/un_association")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> unAssociation(@PathVariable Long datasetId,
                                 @PathVariable Long paragraphId,
                                 @PathVariable Long problemId,
                                 @RequestBody ProblemParagraphMappingBo bo) {
        return toAjax(problemParagraphMappingService.unAssociation(paragraphId, problemId,bo));
    }

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/association")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> association(@PathVariable Long paragraphId,
                                 @PathVariable Long problemId,
                               @PathVariable Long datasetId,
                               @PathVariable Long docId,
                                 @RequestBody ProblemParagraphMappingBo bo) {
        bo.setDatasetId(datasetId);
        bo.setDocumentId(docId);
        return toAjax(problemParagraphMappingService.association(paragraphId, problemId,bo));
    }

}
