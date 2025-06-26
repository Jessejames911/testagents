package com.agents.builder.main.controller;

import java.util.List;

import cn.hutool.core.lang.Assert;
import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.common.core.enums.OperateTargetType;
import com.agents.builder.common.core.enums.OperateType;
import com.agents.builder.common.satoken.annotation.CheckTargetOperate;
import com.agents.builder.main.domain.vo.ParagraphVo;
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
import com.agents.builder.main.domain.vo.ProblemVo;
import com.agents.builder.main.domain.bo.ProblemBo;
import com.agents.builder.main.service.IProblemService;
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
@RequestMapping("/dataset/{datasetId}/problem")
public class ProblemController extends BaseController {

    private final IProblemService problemService;

    /**
     * 查询列表
     */

    @GetMapping("/{pageNum}/{pageSize}")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public TableDataInfo<ProblemVo> list(@PathVariable Integer pageNum,
                                         @PathVariable Integer pageSize,
                                         @PathVariable Long datasetId,
                                         ProblemBo bo, PageQuery pageQuery) {
        pageQuery.setPageNum(pageNum);
        pageQuery.setPageSize(pageSize);
        bo.setDatasetId(datasetId);
        return problemService.queryPageList(bo, pageQuery);
    }


    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/{id}")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<ProblemVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(problemService.queryById(id));
    }


    @GetMapping("/{id}/paragraph")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<List<ParagraphVo>> getParagraph(@NotNull(message = "主键不能为空")
                                @PathVariable Long id) {
        return R.ok(problemService.getParagraph(id));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> add(@PathVariable Long datasetId, @RequestBody List<String> problemList) {
        Assert.notEmpty(problemList,"问题不能为空");
        return toAjax(problemService.insert(datasetId, problemList));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{id}")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> edit(@PathVariable Long id,@PathVariable Long datasetId, @RequestBody ProblemBo bo) {
        bo.setId(id);
        return toAjax(problemService.updateByBo(bo));
    }



    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long id,@PathVariable Long datasetId) {
        return toAjax(problemService.deleteWithValidByIds(List.of(id), true));
    }

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/_batch")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#datasetId"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> batchRemove(@RequestBody List<Long> ids,@PathVariable Long datasetId) {
        return toAjax(problemService.deleteWithValidByIds(ids, true));
    }
}
