package com.agents.builder.main.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.ai.enums.ModelType;
import com.agents.builder.common.ai.strategy.ChatModelBuilder;
import com.agents.builder.common.ai.strategy.EmbeddingModelBuilder;
import com.agents.builder.common.ai.strategy.context.ChatModelBuilderContext;
import com.agents.builder.common.ai.strategy.context.EmbeddingModelBuilderContext;
import com.agents.builder.common.core.enums.StrStatus;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.main.domain.vo.ModelFormVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import com.agents.builder.main.domain.bo.ModelBo;
import com.agents.builder.main.domain.vo.ModelVo;
import com.agents.builder.main.domain.Model;
import com.agents.builder.main.mapper.ModelMapper;
import com.agents.builder.main.service.IModelService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Service业务层处理
 *
 * @author Angus
 * @date 2024-10-31
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ModelServiceImpl implements IModelService {

    private final ModelMapper baseMapper;

    private final EmbeddingModelBuilderContext embeddingModelBuilderContext;

    private final ChatModelBuilderContext chatModelBuilderContext;

    /**
     * 查询
     */
    @Override
    public ModelVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<ModelVo> queryPageList(ModelBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Model> lqw = buildQueryWrapper(bo);
        Page<ModelVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<ModelVo> queryList(ModelBo bo) {
        LambdaQueryWrapper<Model> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Model> buildQueryWrapper(ModelBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Model> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getName()), Model::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getModel_type()), Model::getModel_type, bo.getModel_type());
        lqw.like(StringUtils.isNotBlank(bo.getModelName()), Model::getModelName, bo.getModelName());
        lqw.eq(StringUtils.isNotBlank(bo.getProvider()), Model::getProvider, bo.getProvider());
        lqw.eq(bo.getStatus()!=null, Model::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getPermissionType()), Model::getPermissionType, bo.getPermissionType());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(ModelBo bo) {
        Model add = MapstructUtils.convert(bo, Model.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改
     */
    @Override
    public Boolean updateByBo(ModelBo bo) {
        Model update = MapstructUtils.convert(bo, Model.class);
        Model model = baseMapper.selectById(update.getId());
        update.setProvider(model.getProvider());
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Model entity){
        if (ModelType.EMBEDDING.getKey().equals(entity.getModel_type())){
            EmbeddingModelBuilder service = embeddingModelBuilderContext.getService(entity.getProvider(), () -> new ServiceException("未找到对应的模型构建器"));
            EmbeddingModel embeddingModel = service.build(entity.getCredential().getApiKey(), entity.getCredential().getApiBase(), entity.getModelName());
            try {
                embeddingModel.embed("hello");
            } catch (Exception e) {
                log.error("模型可用性校验失败",e);
                throw new ServiceException("模型可用性校验失败: "+e.getMessage());
            }
            return;
        }

        ChatModelBuilder service = chatModelBuilderContext.getService(entity.getProvider(), () -> new ServiceException("未找到对应的模型构建器"));
        ChatModel chatModel = service.build(entity.getCredential().getApiKey(), entity.getCredential().getApiBase(), entity.getModelName());
        try {
            chatModel.call("hello");
        } catch (Exception e) {
            log.error("模型可用性校验失败",e);
            throw new ServiceException("模型可用性校验失败: "+e.getMessage());
        }
    }

    /**
     * 批量删除
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public List<ModelFormVo> getModelForms(ModelBo bo) {
        ModelFormVo formVo1 = ModelFormVo.builder()
                .inputType("TextInput")
                .label("API 域名")
                .required(false)
                .triggerType("OPTION_LIST")
                .field("api_base")
                .build();
        ModelFormVo formVo2 = BeanUtil.copyProperties(formVo1, ModelFormVo.class);

        formVo2.setField("api_key");
        formVo2.setInputType("PasswordInput");
        formVo2.setLabel("API KEY");
        return List.of(formVo1, formVo2);
    }

    @Override
    public List<ModelVo> getActivedByIdAndType(List<Long> modelIdList, String type) {
        if (CollUtil.isEmpty(modelIdList))return Collections.emptyList();
        return baseMapper.selectVoList(new LambdaQueryWrapper<Model>()
                .in(Model::getId, modelIdList)
                .eq(StringUtils.isNotBlank(type),Model::getModel_type, type)
                .eq(Model::getStatus, StrStatus.SUCCESS.getKey()));
    }

    @Override
    public List<ModelFormVo> getModelParamsForm(Long modelId) {
        ModelFormVo formVo1 = ModelFormVo.builder()
                .inputType("Slider")
                .label("温度")
                .defaultValue(1.0)
                .required(true)
                .triggerType("OPTION_LIST")
                .field("temperature")
                .attrs(Map.of("min",0.1,"max",1.9,"step",0.01,"precision",2,"show-input-controls",false,"show-input",true))
                .build();
        ModelFormVo formVo2 = BeanUtil.copyProperties(formVo1, ModelFormVo.class);

        formVo2.setField("max_tokens");
        formVo2.setLabel("最大Token数");
        formVo2.setDefaultValue(800);
        formVo2.setAttrs(Map.of("min",1,"max",100000,"step",1,"precision",0,"show-input-controls",false,"show-input",true));
        return List.of(formVo1, formVo2);
    }

    @Override
    public Boolean deleteByUser(Long userId) {
        List<Long> modelIds = getByUser(userId).stream().map(Model::getId).collect(Collectors.toList());
        if (CollUtil.isEmpty(modelIds)){
            return true;
        }
        return deleteWithValidByIds(modelIds,true);
    }

    private List<Model> getByUser(Long userId) {
        return baseMapper.selectList(new LambdaQueryWrapper<Model>()
                .eq(Model::getCreateBy,userId));
    }
}
