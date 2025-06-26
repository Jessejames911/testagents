package com.agents.builder.main.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.agents.builder.common.ai.enums.ModelType;
import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.common.core.domain.R;
import com.agents.builder.common.ai.enums.LlmProvider;
import com.agents.builder.main.domain.bo.ModelBo;
import com.agents.builder.main.domain.vo.ModelFormVo;
import com.agents.builder.main.service.IModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final IModelService modelService;

    @GetMapping
    @SaCheckPermission(PermissionConstants.MODEL_READ)
    public R<?> getProviderList(){
        return R.ok(LlmProvider.getAllProvider());
    }

    @GetMapping("/model_type_list")
    @SaCheckPermission(PermissionConstants.MODEL_READ)
    public R<?> getModelTypeList(ModelBo bo) {
        return R.ok(ModelType.getAllType());
    }

    @GetMapping("/model_list")
    @SaCheckPermission(PermissionConstants.MODEL_READ)
    public R<?> getModelList(ModelBo bo) {
        return R.ok(modelService.queryList(bo));
    }

    @GetMapping("/model_form")
    @SaCheckPermission(PermissionConstants.MODEL_READ)
    public R<List<ModelFormVo>> getModelForms(ModelBo bo) {
        return R.ok(modelService.getModelForms(bo));
    }

}
