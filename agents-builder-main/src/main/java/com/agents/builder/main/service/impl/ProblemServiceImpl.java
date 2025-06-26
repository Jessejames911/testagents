package com.agents.builder.main.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.rocketMQ.config.RocketMQConstants;
import com.agents.builder.common.rocketMQ.service.MQProducer;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.agents.builder.common.web.enums.BusinessType;
import com.agents.builder.main.domain.EmbedDocument;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.domain.vo.ProblemParagraphMappingVo;
import com.agents.builder.main.mapper.ParagraphMapper;
import com.agents.builder.main.service.IProblemParagraphMappingService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.agents.builder.main.domain.bo.ProblemBo;
import com.agents.builder.main.domain.vo.ProblemVo;
import com.agents.builder.main.domain.Problem;
import com.agents.builder.main.mapper.ProblemMapper;
import com.agents.builder.main.service.IProblemService;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service业务层处理
 *
 * @author Angus
 * @date 2024-10-31
 */
@RequiredArgsConstructor
@Service
public class ProblemServiceImpl implements IProblemService {

    private final ProblemMapper baseMapper;

    private final IProblemParagraphMappingService paragraphMappingService;

    private final ParagraphMapper paragraphMapper;

    private final MQProducer mqProducer;

    /**
     * 查询
     */
    @Override
    public ProblemVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<ProblemVo> queryPageList(ProblemBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Problem> lqw = buildQueryWrapper(bo);
        Page<ProblemVo> result = baseMapper.getVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<ProblemVo> queryList(ProblemBo bo) {
        LambdaQueryWrapper<Problem> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Problem> buildQueryWrapper(ProblemBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Problem> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getContent()), Problem::getContent, bo.getContent());
        lqw.eq(bo.getHitNum() != null, Problem::getHitNum, bo.getHitNum());
        lqw.eq(bo.getDatasetId() != null, Problem::getDatasetId, bo.getDatasetId());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(ProblemBo bo) {
        Problem add = MapstructUtils.convert(bo, Problem.class);
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
    public Boolean updateByBo(ProblemBo bo) {
        Problem update = MapstructUtils.convert(bo, Problem.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Problem entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }

        // 删除问题向量

        List<ProblemParagraphMappingVo> mappingVos = paragraphMappingService.getByProblemId(ids);

        if (CollUtil.isNotEmpty(mappingVos)) {
            List<Long> mappingIds = mappingVos.stream().map(ProblemParagraphMappingVo::getId).collect(Collectors.toList());

            List<EmbedDocument> embedDocumentList = mappingIds.stream().map(id -> {
                EmbedDocument embedDocument = new EmbedDocument();
                embedDocument.setEmbedId(id.toString());
                embedDocument.setOptType(BusinessType.DELETE);
                embedDocument.setDatasetId(mappingVos.get(0).getDatasetId());
                embedDocument.setUserId(LoginHelper.getUserId());
                return embedDocument;
            }).collect(Collectors.toList());

            mqProducer.sendSecureMsg(RocketMQConstants.DOC_EMBED_TOPIC, embedDocumentList, UUID.randomUUID().toString(),true);

            // 删除分段关联
            paragraphMappingService.deleteWithValidByIds(mappingIds,true);
        }


        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public Boolean insert(Long datasetId, List<String> problemList) {
        List<Problem> problems = problemList.stream().map(item -> {
            Problem problem = new Problem();
            problem.setContent(item);
            problem.setDatasetId(datasetId);
            return problem;
        }).collect(Collectors.toList());

        return baseMapper.insertBatch(problems);
    }

    @Override
    public List<ProblemVo> getByDatasetId(Collection<Long> datasetIds) {
        if (CollUtil.isEmpty(datasetIds)){
            return Collections.emptyList();
        }
        return baseMapper.selectVoList(new LambdaQueryWrapper<Problem>()
                .in(Problem::getDatasetId,datasetIds));
    }

    @Override
    public List<ParagraphVo> getParagraph(Long id) {
        List<Long> paragraphIds = paragraphMappingService.getByProblemId(List.of(id))
                .stream().map(ProblemParagraphMappingVo::getParagraphId)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(paragraphIds)){
            return Collections.emptyList();
        }
        return paragraphMapper.selectVoByIds(paragraphIds);
    }

    @Override
    @Transactional
    public Boolean deleteByDatasetId(Collection<Long> datasetIds) {
        List<ProblemVo> problemVoList = getByDatasetId(datasetIds);
        if (CollUtil.isEmpty(problemVoList)){
            return true;
        }
        return deleteWithValidByIds(problemVoList.stream().map(ProblemVo::getId).collect(Collectors.toList()),true);
    }
}
