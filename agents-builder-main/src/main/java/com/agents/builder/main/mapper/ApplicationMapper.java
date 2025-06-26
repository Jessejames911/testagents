package com.agents.builder.main.mapper;

import com.agents.builder.common.core.enums.OperateTargetType;
import com.agents.builder.common.mybatis.annotation.DataColumn;
import com.agents.builder.common.mybatis.annotation.DataPermission;
import com.agents.builder.main.domain.Application;
import com.agents.builder.main.domain.Application;
import com.agents.builder.main.domain.vo.ApplicationVo;
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
public interface ApplicationMapper extends BaseMapperPlus<Application, ApplicationVo> {

    @Override
    @DataPermission(targetType = OperateTargetType.APP, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    List<Application> selectList(@Param(Constants.WRAPPER)Wrapper<Application> wrapper);

    @Override
    @DataPermission(targetType = OperateTargetType.APP, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    List<Application> selectList(IPage<Application> page, @Param(Constants.WRAPPER) Wrapper<Application> wrapper);

    @DataPermission(targetType = OperateTargetType.APP, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int update(@Param(Constants.ENTITY) Application entity, @Param(Constants.WRAPPER) Wrapper<Application> updateWrapper);

    @DataPermission(targetType = OperateTargetType.APP, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int delete(@Param(Constants.WRAPPER) Wrapper<Application> queryWrapper);

    @DataPermission(targetType = OperateTargetType.APP, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int deleteById(Serializable id);

    @DataPermission(targetType = OperateTargetType.APP, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int deleteBatchIds(@Param(Constants.COLL) Collection<?> idList);

    @DataPermission(targetType = OperateTargetType.APP, value = {
            @DataColumn(key = "target", value = "id"),
            @DataColumn(key = "userName", value = "create_by")
    })
    int updateById(@Param(Constants.ENTITY) Application entity);


}
