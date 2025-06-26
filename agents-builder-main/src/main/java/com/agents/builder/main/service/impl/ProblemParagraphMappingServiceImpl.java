package com.agents.builder.main.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.rocketMQ.config.RocketMQConstants;
import com.agents.builder.common.rocketMQ.service.MQProducer;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.agents.builder.common.web.enums.BusinessType;
import com.agents.builder.main.domain.EmbedDocument;
import com.agents.builder.main.domain.Problem;
import com.agents.builder.main.domain.bo.ProblemBo;
import com.agents.builder.main.mapper.ProblemMapper;
import com.agents.builder.main.util.DocConvert;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.agents.builder.main.domain.bo.ProblemParagraphMappingBo;
import com.agents.builder.main.domain.vo.ProblemParagraphMappingVo;
import com.agents.builder.main.domain.ProblemParagraphMapping;
import com.agents.builder.main.mapper.ProblemParagraphMappingMapper;
import com.agents.builder.main.service.IProblemParagraphMappingService;

import java.util.*;

/**
 * Service业务层处理
 *
 * @author Angus
 * @date 2024-10-31
 */
@RequiredArgsConstructor
@Service
public class ProblemParagraphMappingServiceImpl implements IProblemParagraphMappingService {

    private final ProblemParagraphMappingMapper baseMapper;

    private final MQProducer mqProducer;

    private final ProblemMapper problemMapper;

    /**
     * 查询
     */
    @Override
    public ProblemParagraphMappingVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<ProblemParagraphMappingVo> queryPageList(ProblemParagraphMappingBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProblemParagraphMapping> lqw = buildQueryWrapper(bo);
        Page<ProblemParagraphMappingVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<ProblemParagraphMappingVo> queryList(ProblemParagraphMappingBo bo) {
        LambdaQueryWrapper<ProblemParagraphMapping> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ProblemParagraphMapping> buildQueryWrapper(ProblemParagraphMappingBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ProblemParagraphMapping> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDatasetId() != null, ProblemParagraphMapping::getDatasetId, bo.getDatasetId());
        lqw.eq(bo.getDocumentId() != null, ProblemParagraphMapping::getDocumentId, bo.getDocumentId());
        lqw.eq(bo.getParagraphId() != null, ProblemParagraphMapping::getParagraphId, bo.getParagraphId());
        lqw.eq(bo.getProblemId() != null, ProblemParagraphMapping::getProblemId, bo.getProblemId());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(ProblemParagraphMappingBo bo) {
        ProblemParagraphMapping add = MapstructUtils.convert(bo, ProblemParagraphMapping.class);
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
    public Boolean updateByBo(ProblemParagraphMappingBo bo) {
        ProblemParagraphMapping update = MapstructUtils.convert(bo, ProblemParagraphMapping.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProblemParagraphMapping entity){
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
        if (CollUtil.isEmpty(ids))return true;

        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public List<ProblemParagraphMappingVo> getByParagraphId(Collection<Long> paragraphId) {
        if (CollUtil.isEmpty(paragraphId)){
            return Collections.emptyList();
        }
        return baseMapper.selectVoList(new LambdaQueryWrapper<ProblemParagraphMapping>()
                .in(ProblemParagraphMapping::getParagraphId, paragraphId));
    }

    @Override
    public List<ProblemParagraphMappingVo> getByProblemId(Collection<Long> problemIds) {
        if (CollUtil.isEmpty(problemIds)){
            return Collections.emptyList();
        }
        return baseMapper.selectVoList(new LambdaQueryWrapper<ProblemParagraphMapping>()
                .in(ProblemParagraphMapping::getProblemId, problemIds));
    }

    @Override
    public Boolean unAssociation(Long paragraphId, Long problemId, ProblemParagraphMappingBo bo) {

        Problem problem = problemMapper.selectById(problemId);

        if (problem == null) return true;

        ProblemParagraphMapping mapping = baseMapper.selectOne(new LambdaQueryWrapper<ProblemParagraphMapping>()
                .eq(ProblemParagraphMapping::getProblemId, problemId)
                .eq(ProblemParagraphMapping::getParagraphId, paragraphId));

        deleteProblemVector( mapping.getId(),problem.getDatasetId());

        return baseMapper.deleteById(mapping.getId()) > 0;
    }

    private void deleteProblemVector(Long mappingId,Long datasetId) {

        EmbedDocument embedDocument = new EmbedDocument();
        embedDocument.setEmbedId(mappingId.toString());
        embedDocument.setOptType(BusinessType.DELETE);
        embedDocument.setDatasetId(datasetId);
        embedDocument.setUserId(LoginHelper.getUserId());

        mqProducer.sendSecureMsg(RocketMQConstants.DOC_EMBED_TOPIC, List.of(embedDocument), UUID.randomUUID().toString(),true);
    }

    @Override
    public Boolean association(Long paragraphId, Long problemId, ProblemParagraphMappingBo bo) {
        long mappingId = IdWorker.getId();
        ProblemParagraphMapping paragraphMapping = new ProblemParagraphMapping();
        paragraphMapping.setParagraphId(paragraphId);
        paragraphMapping.setProblemId(problemId);
        paragraphMapping.setDatasetId(bo.getDatasetId());
        paragraphMapping.setDocumentId(bo.getDocumentId());

        Problem problem = problemMapper.selectById(problemId);

        if (problem == null) return true;
        ProblemBo problemBo = BeanUtil.toBean(problem, ProblemBo.class);
        problemBo.setParagraphId(paragraphId);
        problemBo.setMappingId(mappingId);
        addProblemVector(problemBo);
        return baseMapper.insert(paragraphMapping) > 0;
    }

    private void addProblemVector(ProblemBo problemBo) {
        List<EmbedDocument> embedDocuments = DocConvert.problemBo2EmbedDoc(List.of(problemBo));
        embedDocuments.forEach(embedDocument -> embedDocument.setOptType(BusinessType.INSERT));
        mqProducer.sendSecureMsg(RocketMQConstants.DOC_EMBED_TOPIC, embedDocuments, UUID.randomUUID().toString(),true);
    }

    @Override
    public Boolean deleteByParagraphId(Collection<Long> paragraphIds) {
        if (CollUtil.isEmpty(paragraphIds)){
            return true;
        }
        return baseMapper.delete(new LambdaQueryWrapper<ProblemParagraphMapping>()
                .in(ProblemParagraphMapping::getParagraphId, paragraphIds)) > 0;
    }

    @Override
    public Boolean deleteByProblemId(Collection<Long> problemIds) {
        if (CollUtil.isEmpty(problemIds)){
            return true;
        }
        return baseMapper.delete(new LambdaQueryWrapper<ProblemParagraphMapping>()
                .in(ProblemParagraphMapping::getProblemId, problemIds)) > 0;
    }

    @Override
    public Boolean insertBatch(List<ProblemParagraphMapping> mappingList) {
        return baseMapper.insertBatch(mappingList);
    }

    @Override
    public List<ProblemBo> getProblemByParagraphId(List<Long> paragraphIds) {
        return problemMapper.selectProblemByParagraphIds(paragraphIds);
    }
}
