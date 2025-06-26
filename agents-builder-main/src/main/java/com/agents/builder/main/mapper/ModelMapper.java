package com.agents.builder.main.mapper;

import com.agents.builder.common.core.enums.OperateTargetType;
import com.agents.builder.common.mybatis.annotation.DataColumn;
import com.agents.builder.common.mybatis.annotation.DataPermission;
import com.agents.builder.main.domain.Model;
import com.agents.builder.main.domain.vo.ModelVo;
import com.agents.builder.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Mapper接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface ModelMapper extends BaseMapperPlus<Model, ModelVo> {

    @Override
    @DataPermission(targetType = OperateTargetType.MODEL, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    List<Model> selectList(@Param(Constants.WRAPPER)Wrapper<Model> wrapper);

    @Override
    @DataPermission(targetType = OperateTargetType.MODEL, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    List<Model> selectList(IPage<Model> page, @Param(Constants.WRAPPER)Wrapper<Model> wrapper);

    @DataPermission(targetType = OperateTargetType.MODEL, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int update(@Param(Constants.ENTITY) Model entity, @Param(Constants.WRAPPER) Wrapper<Model> updateWrapper);

    @DataPermission(targetType = OperateTargetType.MODEL, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int delete(@Param(Constants.WRAPPER) Wrapper<Model> queryWrapper);

    @DataPermission(targetType = OperateTargetType.MODEL, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int deleteById(Serializable id);

    @DataPermission(targetType = OperateTargetType.MODEL, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int deleteBatchIds(@Param(Constants.COLL) Collection<?> idList);

    @DataPermission(targetType = OperateTargetType.MODEL, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int updateById(@Param(Constants.ENTITY) Model entity);
}
