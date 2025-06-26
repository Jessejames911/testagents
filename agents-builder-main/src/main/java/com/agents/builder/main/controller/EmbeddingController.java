package com.agents.builder.main.controller;

import java.util.List;

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
import com.agents.builder.main.domain.vo.EmbeddingVo;
import com.agents.builder.main.domain.bo.EmbeddingBo;
import com.agents.builder.main.service.IEmbeddingService;
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
@RequestMapping("/embedding")
public class EmbeddingController extends BaseController {

    private final IEmbeddingService embeddingService;

    /**
     * 查询列表
     */

    @GetMapping("/list")
    public TableDataInfo<EmbeddingVo> list(EmbeddingBo bo, PageQuery pageQuery) {
        return embeddingService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出列表
     */

    @Log(title = "", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(EmbeddingBo bo, HttpServletResponse response) {
        List<EmbeddingVo> list = embeddingService.queryList(bo);
        ExcelUtil.exportExcel(list, "", EmbeddingVo.class, response);
    }

    /**
     * 获取详细信息
     *
     * @param id 主键
     */

    @GetMapping("/{id}")
    public R<EmbeddingVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(embeddingService.queryById(id));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody EmbeddingBo bo) {
        return toAjax(embeddingService.insertByBo(bo));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody EmbeddingBo bo) {
        return toAjax(embeddingService.updateByBo(bo));
    }

    /**
     * 删除
     *
     * @param ids 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(embeddingService.deleteWithValidByIds(List.of(ids), true));
    }
}
