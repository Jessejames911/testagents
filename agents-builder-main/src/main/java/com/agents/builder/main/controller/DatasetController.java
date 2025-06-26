package com.agents.builder.main.controller;

import java.util.List;

import com.agents.builder.common.ai.enums.ModelType;
import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.common.core.enums.OperateTargetType;
import com.agents.builder.common.core.enums.OperateType;
import com.agents.builder.common.satoken.annotation.CheckTargetOperate;
import com.agents.builder.main.domain.bo.ModelBo;
import com.agents.builder.main.domain.dto.SearchDto;
import com.agents.builder.main.domain.vo.*;
import com.agents.builder.main.service.IModelService;
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
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.core.validate.EditGroup;
import com.agents.builder.common.web.enums.BusinessType;
import com.agents.builder.main.domain.bo.DatasetBo;
import com.agents.builder.main.service.IDatasetService;
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
public class DatasetController extends BaseController {

    private final IDatasetService datasetService;

    private final IModelService modelService;

    /**
     * 查询列表
     */
    @GetMapping("/{pageNum}/{pageSize}")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public TableDataInfo<DatasetVo> list(@PathVariable Integer pageNum,
                                         @PathVariable Integer pageSize,
                                         DatasetBo bo, PageQuery pageQuery) {
        pageQuery.setPageNum(pageNum);
        pageQuery.setPageSize(pageSize);
        return datasetService.queryPageList(bo, pageQuery);
    }

    @GetMapping
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<List<DatasetVo>> getAll(DatasetBo bo) {
        return R.ok(datasetService.queryList(bo));
    }


    @GetMapping("/{id}/application")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<List<ApplicationVo>> getApplication(@PathVariable Long id) {
        return R.ok(datasetService.getApplication(id));
    }


    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/{id}")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<DatasetVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(datasetService.queryById(id));
    }

    @GetMapping("/{id}/model")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<List<ModelVo>> getModel() {
        ModelBo modelBo = new ModelBo();
        modelBo.setModel_type(ModelType.LLM.getKey());
        return R.ok(modelService.queryList(modelBo));
    }

    @GetMapping("/{id}/hit_test")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<List<ParagraphVo>> hitTest(@NotNull(message = "主键不能为空")
                                @PathVariable Long id, SearchDto dto) {
        dto.setDatasetIdList(List.of(id));
        return R.ok(datasetService.hitTest(dto));
    }

    @GetMapping("/{id}/problem")
    @SaCheckPermission(PermissionConstants.DATASET_READ)
    public R<List<ProblemVo>> getProblem(@NotNull(message = "主键不能为空")
                                @PathVariable Long id) {
        return R.ok(datasetService.getProblem(id));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    @SaCheckPermission(PermissionConstants.DATASET_CREATE)
    public R<DatasetVo> add(@Validated(AddGroup.class) @RequestBody DatasetBo bo) {
        return R.ok(datasetService.insertByBo(bo));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/{id}")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#id"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> edit(@PathVariable Long id,@Validated(EditGroup.class) @RequestBody DatasetBo bo) {
        bo.setId(id);
        return toAjax(datasetService.updateByBo(bo));
    }

    @RepeatSubmit(interval = 1000 * 60 * 5, message = "操作过于频繁")
    @PutMapping("/{id}/re_embedding")
    @SaCheckPermission(PermissionConstants.DATASET_UPDATE)
    @CheckTargetOperate(value = {"#id"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> reEmbedding(@PathVariable Long id) {
        return toAjax(datasetService.reEmbedding(id));
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @SaCheckPermission(PermissionConstants.DATASET_DELETE)
    @CheckTargetOperate(value = {"#ids"},operateType = OperateType.MANAGE,targetType = OperateTargetType.DATASET)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long ids) {
        return toAjax(datasetService.deleteWithValidByIds(List.of(ids), true));
    }
}
