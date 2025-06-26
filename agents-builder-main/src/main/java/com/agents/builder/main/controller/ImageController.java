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
import com.agents.builder.main.domain.vo.ImageVo;
import com.agents.builder.main.domain.bo.ImageBo;
import com.agents.builder.main.service.IImageService;
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
@RequestMapping("/image")
public class ImageController extends BaseController {

    private final IImageService imageService;

    /**
     * 查询列表
     */

    @GetMapping("/list")
    public TableDataInfo<ImageVo> list(ImageBo bo, PageQuery pageQuery) {
        return imageService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出列表
     */

    @Log(title = "", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ImageBo bo, HttpServletResponse response) {
        List<ImageVo> list = imageService.queryList(bo);
        ExcelUtil.exportExcel(list, "", ImageVo.class, response);
    }

    /**
     * 获取详细信息
     *
     * @param 主键
     */

    @GetMapping("/{id}")
    public R<ImageVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(imageService.queryById(id));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ImageBo bo) {
        return toAjax(imageService.insertByBo(bo));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ImageBo bo) {
        return toAjax(imageService.updateByBo(bo));
    }

    /**
     * 删除
     *
     * @param createTimes 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long ids) {
        return toAjax(imageService.deleteWithValidByIds(List.of(ids), true));
    }
}
