package com.agents.builder.main.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.script.ScriptExecutor;
import com.agents.builder.common.script.context.ScriptExecutorContext;
import com.agents.builder.common.script.enums.ScriptLanguage;
import com.agents.builder.main.domain.dto.FunctionDebugDto;
import com.agents.builder.main.domain.vo.FunctionDebugVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.agents.builder.main.domain.bo.FunctionLibBo;
import com.agents.builder.main.domain.vo.FunctionLibVo;
import com.agents.builder.main.domain.FunctionLib;
import com.agents.builder.main.mapper.FunctionLibMapper;
import com.agents.builder.main.service.IFunctionLibService;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service业务层处理
 *
 * @author Angus
 * @date 2024-10-31
 */
@RequiredArgsConstructor
@Service
public class FunctionLibServiceImpl implements IFunctionLibService {

    private final FunctionLibMapper baseMapper;

    private final ScriptExecutorContext scriptExecutorContext;

    /**
     * 查询
     */
    @Override
    public FunctionLibVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<FunctionLibVo> queryPageList(FunctionLibBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<FunctionLib> lqw = buildQueryWrapper(bo);
        Page<FunctionLibVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<FunctionLibVo> queryList(FunctionLibBo bo) {
        LambdaQueryWrapper<FunctionLib> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<FunctionLib> buildQueryWrapper(FunctionLibBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<FunctionLib> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), FunctionLib::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getCode()), FunctionLib::getCode, bo.getCode());
        lqw.eq(bo.getIsActive() != null, FunctionLib::getIsActive, bo.getIsActive());
        lqw.eq(StringUtils.isNotBlank(bo.getPermissionType()), FunctionLib::getPermissionType, bo.getPermissionType());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public FunctionLibVo insertByBo(FunctionLibBo bo) {
        FunctionLib add = MapstructUtils.convert(bo, FunctionLib.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return queryById(add.getId());
    }

    /**
     * 修改
     */
    @Override
    public Boolean updateByBo(FunctionLibBo bo) {
        FunctionLib update = MapstructUtils.convert(bo, FunctionLib.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(FunctionLib entity){
        //TODO 做一些数据校验,如唯一约束
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
    public Object debug(FunctionDebugDto dto) {
        Map<String, Object> params = getParams(dto.getInputFieldList(),dto.getDebugFieldList());

        ScriptExecutor scriptExecutor = scriptExecutorContext.getService(Optional.ofNullable(dto.getLanguage()).orElse(ScriptLanguage.JAVA.getKey()), () -> new ServiceException("暂不支持脚本语言" + dto.getLanguage()));

        try {
            return scriptExecutor.execute(dto.getCode(), params);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }

    }

    @Override
    public FunctionDebugVo pylint(FunctionDebugDto dto) {
        ScriptExecutor scriptExecutor = scriptExecutorContext.getService(Optional.ofNullable(dto.getLanguage()).orElse(ScriptLanguage.PYTHON.getKey()), () -> new ServiceException("暂不支持脚本语言" + dto.getLanguage()));
        try {
            scriptExecutor.compile(dto.getCode());
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        return null;
    }

    @Override
    public Boolean deleteByUser(Long userId) {
        List<Long> functionIds = getByUser(userId).stream().map(FunctionLib::getId).collect(Collectors.toList());
        if (CollUtil.isEmpty(functionIds)){
            return true;
        }
        return deleteWithValidByIds(functionIds,true);
    }

    private List<FunctionLib> getByUser(Long userId) {
        return baseMapper.selectList(new LambdaQueryWrapper<FunctionLib>()
                .eq(FunctionLib::getCreateBy,userId));
    }

    private Map<String, Object> getParams(List<Map<String, Object>> inputFieldList, List<Map<String, Object>> debugFieldList) {
        return debugFieldList.stream().collect(Collectors.toMap(k -> (String) k.get("name"), v -> v.get("value")));
    }


}
