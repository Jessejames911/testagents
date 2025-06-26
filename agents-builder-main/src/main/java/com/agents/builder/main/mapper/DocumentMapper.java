package com.agents.builder.main.mapper;

import com.agents.builder.main.domain.Document;
import com.agents.builder.main.domain.vo.DocumentVo;
import com.agents.builder.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * Mapper接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface DocumentMapper extends BaseMapperPlus<Document, DocumentVo> {

    Page<DocumentVo> getVoPage(@Param("page")Page<DocumentVo> page, @Param(Constants.WRAPPER) Wrapper<Document> lqw);
}
