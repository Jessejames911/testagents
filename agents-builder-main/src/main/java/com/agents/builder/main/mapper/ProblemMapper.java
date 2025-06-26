package com.agents.builder.main.mapper;

import com.agents.builder.main.domain.Problem;
import com.agents.builder.main.domain.bo.ProblemBo;
import com.agents.builder.main.domain.vo.ProblemVo;
import com.agents.builder.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface ProblemMapper extends BaseMapperPlus<Problem, ProblemVo> {

    List<ProblemBo> selectProblemByParagraphIds(@Param("list") List<Long> paragraphIds);

    Page<ProblemVo> getVoPage(@Param("page") Page<ProblemVo> page, @Param(Constants.WRAPPER) Wrapper<Problem> lqw);
}
