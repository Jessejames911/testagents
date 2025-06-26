package com.agents.builder.main.mapper;

import com.agents.builder.common.core.enums.OperateTargetType;
import com.agents.builder.common.mybatis.annotation.DataColumn;
import com.agents.builder.common.mybatis.annotation.DataPermission;
import com.agents.builder.main.domain.Dataset;
import com.agents.builder.main.domain.vo.DatasetVo;
import com.agents.builder.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public interface DatasetMapper extends BaseMapperPlus<Dataset, DatasetVo> {

    @DataPermission(targetType = OperateTargetType.DATASET, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    Page<DatasetVo> getVoPage(@Param("page") Page<DatasetVo> page,  @Param(Constants.WRAPPER)Wrapper<Dataset> lqw);

    @Override
    @DataPermission(targetType = OperateTargetType.DATASET, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
   List<Dataset> selectList(@Param(Constants.WRAPPER) Wrapper<Dataset> wrapper);

    @DataPermission(targetType = OperateTargetType.DATASET, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int update(@Param(Constants.ENTITY) Dataset entity, @Param(Constants.WRAPPER) Wrapper<Dataset> updateWrapper);

    @DataPermission(targetType = OperateTargetType.DATASET, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int delete(@Param(Constants.WRAPPER) Wrapper<Dataset> queryWrapper);

    @DataPermission(targetType = OperateTargetType.DATASET, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int deleteById(Serializable id);

    @DataPermission(targetType = OperateTargetType.DATASET, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int deleteBatchIds(@Param(Constants.COLL) Collection<?> idList);

    @DataPermission(targetType = OperateTargetType.DATASET, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int updateById(@Param(Constants.ENTITY) Dataset entity);
}
