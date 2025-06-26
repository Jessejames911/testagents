package com.agents.builder.main.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.agents.builder.common.ai.strategy.context.ChatModelBuilderContext;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.rocketMQ.config.RocketMQConstants;
import com.agents.builder.common.rocketMQ.service.MQProducer;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.agents.builder.common.web.enums.BusinessType;
import com.agents.builder.main.domain.EmbedDocument;
import com.agents.builder.main.domain.Paragraph;
import com.agents.builder.main.domain.Problem;
import com.agents.builder.main.domain.ProblemParagraphMapping;
import com.agents.builder.main.domain.bo.ParagraphBo;
import com.agents.builder.main.domain.bo.ProblemBo;
import com.agents.builder.main.domain.bo.ProblemParagraphMappingBo;
import com.agents.builder.main.domain.dto.DocGenerateRelatedDto;
import com.agents.builder.main.domain.vo.ModelVo;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.domain.vo.ProblemParagraphMappingVo;
import com.agents.builder.main.domain.vo.ProblemVo;
import com.agents.builder.main.mapper.ModelMapper;
import com.agents.builder.main.mapper.ParagraphMapper;
import com.agents.builder.main.mapper.ProblemMapper;
import com.agents.builder.main.service.IParagraphService;
import com.agents.builder.main.service.IProblemParagraphMappingService;
import com.agents.builder.main.util.DocConvert;
import com.agents.builder.main.util.EmbeddingUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service业务层处理
 *
 * @author Angus
 * @date 2024-10-31
 */
@RequiredArgsConstructor
@Service
public class ParagraphServiceImpl implements IParagraphService {

    private final ParagraphMapper baseMapper;

    private final IProblemParagraphMappingService problemParagraphMappingService;

    private final ProblemMapper problemMapper;

    private final MQProducer mqProducer;

    private final ChatModelBuilderContext chatModelBuilderContext;

    private final ModelMapper modelMapper;

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;


    /**
     * 查询
     */
    @Override
    public ParagraphVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<ParagraphVo> queryPageList(ParagraphBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Paragraph> lqw = buildQueryWrapper(bo);
        Page<ParagraphVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        result.getRecords().forEach(item -> item.setCharLength(item.getContent().length()));
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<ParagraphVo> queryList(ParagraphBo bo) {
        LambdaQueryWrapper<Paragraph> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Paragraph> buildQueryWrapper(ParagraphBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Paragraph> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getContent()), Paragraph::getContent, bo.getContent());
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), Paragraph::getTitle, bo.getTitle());
        lqw.eq(bo.getStatus() != null, Paragraph::getStatus, bo.getStatus());
        lqw.eq(bo.getHitNum() != null, Paragraph::getHitNum, bo.getHitNum());
        lqw.eq(bo.getIsActive() != null, Paragraph::getIsActive, bo.getIsActive());
        lqw.eq(bo.getDatasetId() != null, Paragraph::getDatasetId, bo.getDatasetId());
        lqw.eq(bo.getDocumentId() != null, Paragraph::getDocumentId, bo.getDocumentId());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(ParagraphBo bo) {
        bo.setId(IdWorker.getId());
        Paragraph add = MapstructUtils.convert(bo, Paragraph.class);
        validEntityBeforeSave(add);

        add.setIsActive(true);

        List<EmbedDocument> embedDocumentList = DocConvert.paragraph2EmbedDoc(List.of(add));

        List<Problem> problemList = new ArrayList<>();
        List<ProblemParagraphMapping> mappingList = new ArrayList<>();

        if (CollUtil.isNotEmpty(bo.getProblemList())) {
            bo.getProblemList().forEach(problemBo -> {
                problemBo.setId(IdWorker.getId());
                problemBo.setDatasetId(add.getDatasetId());
                problemBo.setDocumentId(add.getDocumentId());
                problemBo.setParagraphId(add.getId());
                problemBo.setMappingId(IdWorker.getId());

                problemList.add(MapstructUtils.convert(problemBo, Problem.class));
            });

            embedDocumentList.addAll(DocConvert.problemBo2EmbedDoc(bo.getProblemList()));

            buildProblemAndMappings(mappingList, bo.getProblemList());
        }

        // 向量化
        embedDocumentList.forEach(embedDocument -> embedDocument.setOptType(BusinessType.INSERT));

        // 向量化
        mqProducer.sendSecureMsg(RocketMQConstants.DOC_EMBED_TOPIC, embedDocumentList, UUID.randomUUID().toString(), true);


        if (CollUtil.isNotEmpty(bo.getProblemList())) {
            problemMapper.insertBatch(problemList);
            problemParagraphMappingService.insertBatch(mappingList);
        }

        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    @Override
    @Transactional
    public Boolean insertBatchByBo(List<ParagraphBo> paragraphBos) {
        List<Paragraph> paragraphList = new ArrayList<>();

        List<Problem> problemList = new ArrayList<>();

        List<ProblemBo> problemBoList = new ArrayList<>();

        List<ProblemParagraphMapping> mappingList = new ArrayList<>();
        paragraphBos.forEach(bo -> {
            bo.setId(IdWorker.getId());
            Paragraph paragraph = MapstructUtils.convert(bo, Paragraph.class);
            paragraphList.add(paragraph);

            if (CollUtil.isNotEmpty(bo.getProblemList())) {
                bo.getProblemList().forEach(problemBo -> {
                    problemBo.setId(IdWorker.getId());
                    problemBo.setDatasetId(paragraph.getDatasetId());
                    problemBo.setDocumentId(paragraph.getDocumentId());
                    problemBo.setParagraphId(paragraph.getId());
                    problemBo.setMappingId(IdWorker.getId());
                    problemList.add(MapstructUtils.convert(problemBo, Problem.class));
                });

                problemBoList.addAll(bo.getProblemList());
                buildProblemAndMappings(mappingList, bo.getProblemList());
            }
        });

        List<EmbedDocument> embedDocumentList = DocConvert.paragraph2EmbedDoc(paragraphList);

        embedDocumentList.addAll(DocConvert.problemBo2EmbedDoc(problemBoList));

        embedDocumentList.forEach(embedDocument -> embedDocument.setOptType(BusinessType.INSERT));

        // 向量化
        mqProducer.sendSecureMsg(RocketMQConstants.DOC_EMBED_TOPIC, embedDocumentList, UUID.randomUUID().toString(), true);

        problemMapper.insertBatch(problemList);

        problemParagraphMappingService.insertBatch(mappingList);

        return baseMapper.insertBatch(paragraphList);

    }

    @Override
    public Boolean updateIsActiveByDocId(Long docId, Boolean isActive) {
        return baseMapper.update(new LambdaUpdateWrapper<Paragraph>()
                .set(Paragraph::getIsActive, isActive)
                .eq(Paragraph::getDocumentId, docId)) > 0;
    }

    @Override
    public List<EmbedDocument> getEmbedDocuments(List<Long> paragraphIds) {
        List<Paragraph> paragraphList = baseMapper.selectBatchIds(paragraphIds);
        List<EmbedDocument> embedDocumentList = DocConvert.paragraph2EmbedDoc(paragraphList);
        List<ProblemBo> problemList = problemParagraphMappingService.getProblemByParagraphId(paragraphList.stream().map(Paragraph::getId).collect(Collectors.toList()));
        embedDocumentList.addAll(DocConvert.problemBo2EmbedDoc(problemList));
        return embedDocumentList;
    }

    @Override
    public Boolean update(Wrapper<Paragraph> updateWrapper) {
        return baseMapper.update(updateWrapper) > 0;
    }

    @Override
    public List<Paragraph> getByDocumentIds(List<Long> documentIdList) {
        if (CollUtil.isEmpty(documentIdList)) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<Paragraph>()
                .in(Paragraph::getDocumentId, documentIdList));
    }

    @Override
    public Boolean batchGenerateRelated(DocGenerateRelatedDto dto) {
        if (CollUtil.isEmpty(dto.getParagraphIdList())) return true;
        ModelVo modelVo = modelMapper.selectVoById(dto.getModelId());
        ChatModel chatModel = chatModelBuilderContext.getService(modelVo.getProvider(), () -> new ServiceException("未找到匹配的模型构建器")).build(modelVo.getCredential().getApiKey(), modelVo.getCredential().getApiBase(), modelVo.getModelName());

        List<Paragraph> paragraphList = baseMapper.selectBatchIds(dto.getParagraphIdList());

        List<CompletableFuture<Void>> futureList = paragraphList.stream().map(paragraph ->
                CompletableFuture.runAsync(() -> {
                    String answer = chatModel.call(dto.getPrompt().replace("{data}", paragraph.getContent()));
                    List<String> problemList = Arrays.stream(answer.replace("<question>", "").split("</question>")).toList();
                    addProblemList(problemList, paragraph.getId(), dto.getDatasetId(), dto.getDocumentId());
                }, threadPoolTaskExecutor)).collect(Collectors.toList());

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
        return true;
    }

    @Override
    @Transactional
    public Boolean migrate(List<Long> paragraphIdList, Long sourceDataset, Long targetDataset, Long sourceDocument, Long targetDocument) {

        Assert.notEmpty(paragraphIdList, "paragraphIdList is empty");
        Assert.notNull(sourceDataset, "sourceDataset is null");
        Assert.notNull(targetDataset, "targetDataset is null");
        Assert.notNull(sourceDocument, "sourceDocument is null");
        Assert.notNull(targetDocument, "targetDocument is null");

        if (!sourceDataset.equals(targetDataset)) {
            List<EmbedDocument> embedDocuments = getEmbedDocuments(paragraphIdList);

            // 迁移向量库
            VectorStore sourceVectorStore = EmbeddingUtil.getVectorStoreByDatasetId(sourceDataset);

            VectorStore targetVectorStore = EmbeddingUtil.getVectorStoreByDatasetId(targetDataset);

            sourceVectorStore.delete(embedDocuments.stream().map(EmbedDocument::getEmbedId).collect(Collectors.toList()));

            Map<Long, EmbedDocument> embedProblemMap = embedDocuments.stream().filter(embedDocument -> embedDocument.getProblemId() != null).collect(Collectors.toMap(k -> k.getProblemId(), v -> v, (v1, v2) -> v1));

            // 删除旧关联
            problemParagraphMappingService.deleteWithValidByIds(embedProblemMap.values().stream().map(item->Long.parseLong(item.getEmbedId())).collect(Collectors.toList()), true);

            // 复制关联问题
            List<ProblemParagraphMapping> mappingList = new ArrayList<>();

            List<Problem> problemList = new ArrayList<>();

            List<ProblemBo> problemBoList = embedProblemMap.keySet().stream().map(item -> {
                EmbedDocument embedDocument = embedProblemMap.get(item);

                ProblemBo problemBo = new ProblemBo();
                problemBo.setId(IdWorker.getId());
                problemBo.setDatasetId(targetDataset);
                problemBo.setDocumentId(targetDocument);
                problemBo.setParagraphId(embedDocument.getParagraphId());
                problemBo.setMappingId(IdWorker.getId());
                problemBo.setContent(embedDocument.getContent());

                embedDocument.setDatasetId(targetDataset);
                embedDocument.setDocumentId(targetDocument);
                embedDocument.setEmbedId(problemBo.getMappingId().toString());
                embedDocument.setProblemId(problemBo.getId());
                problemList.add(MapstructUtils.convert(problemBo, Problem.class));
                return problemBo;
            }).collect(Collectors.toList());

            embedDocuments.forEach(item -> {

                EmbedDocument embedDocument = embedProblemMap.get(item.getProblemId());

                item.setDatasetId(targetDataset);
                item.setDocumentId(targetDocument);
                if (embedDocument!=null) {
                    // 替换复制后的问题信息
                    item.setEmbedId(embedDocument.getEmbedId());
                    item.setProblemId(embedDocument.getProblemId());
                }
            });

            buildProblemAndMappings(mappingList, problemBoList);
            targetVectorStore.add(DocConvert.convertDocuments(embedDocuments));

            problemMapper.insertBatch(problemList);

            // 新增新关联
            problemParagraphMappingService.insertBatch(mappingList);
        }


        // 更新分段所属
        return update(new LambdaUpdateWrapper<Paragraph>()
                .set(Paragraph::getDatasetId, targetDataset)
                .set(Paragraph::getDocumentId, targetDocument)
                .in(Paragraph::getId, paragraphIdList));
    }


    private void buildProblemAndMappings(List<ProblemParagraphMapping> mappingList, List<ProblemBo> problemBoList) {
        problemBoList.forEach(problemBo -> {

            ProblemParagraphMapping mapping = new ProblemParagraphMapping();
            mapping.setId(problemBo.getMappingId());
            mapping.setParagraphId(problemBo.getParagraphId());
            mapping.setDatasetId(problemBo.getDatasetId());
            mapping.setDocumentId(problemBo.getDocumentId());
            mapping.setProblemId(problemBo.getId());
            mappingList.add(mapping);
        });
    }


    /**
     * 修改
     */
    @Override
    public Boolean updateByBo(ParagraphBo bo) {
        Paragraph update = MapstructUtils.convert(bo, Paragraph.class);
        validEntityBeforeSave(update);

        Paragraph paragraph = baseMapper.selectById(update.getId());


        if (bo.getContent() != null && !paragraph.getContent().equals(bo.getContent())) {
            paragraph.setContent(bo.getContent());
            List<EmbedDocument> embedDocumentList = DocConvert.paragraph2EmbedDoc(List.of(paragraph));
            embedDocumentList.forEach(embedDocument -> embedDocument.setOptType(BusinessType.UPDATE));
            mqProducer.sendSecureMsg(RocketMQConstants.DOC_EMBED_TOPIC, embedDocumentList, UUID.randomUUID().toString(), true);
        }

        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Paragraph entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        Long datasetId = baseMapper.selectBatchIds(ids).get(0).getDatasetId();
        // 删除段落向量与问题向量

        List<Long> mappingIds = problemParagraphMappingService.getByParagraphId(ids).stream().map(ProblemParagraphMappingVo::getId).collect(Collectors.toList());

        mappingIds.addAll(ids);

        List<EmbedDocument> embedDocuments = mappingIds.stream().map(item -> {
            EmbedDocument embedDocument = new EmbedDocument();
            embedDocument.setEmbedId(item.toString());
            embedDocument.setOptType(BusinessType.DELETE);
            embedDocument.setDatasetId(datasetId);
            embedDocument.setUserId(LoginHelper.getUserId());
            return embedDocument;
        }).collect(Collectors.toList());

        mqProducer.sendSecureMsg(RocketMQConstants.DOC_EMBED_TOPIC, embedDocuments, UUID.randomUUID().toString(),true);

        // 删除问题关联
        problemParagraphMappingService.deleteByParagraphId(ids);

        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public List<ProblemVo> getProblem(Long id) {
        List<Long> problemIds = problemParagraphMappingService.getByParagraphId(List.of(id))
                .stream().map(ProblemParagraphMappingVo::getProblemId)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(problemIds)) {
            return Collections.emptyList();
        }
        return problemMapper.selectVoBatchIds(problemIds);
    }

    @Override
    @Transactional
    public Boolean addProblem(ProblemBo bo) {
        long mappingId = IdWorker.getId();
        bo.setMappingId(mappingId);
        ProblemParagraphMappingBo problemParagraphMappingBo = new ProblemParagraphMappingBo();
        problemParagraphMappingBo.setParagraphId(bo.getParagraphId());
        problemParagraphMappingBo.setProblemId(bo.getId());
        problemParagraphMappingBo.setDatasetId(bo.getDatasetId());
        problemParagraphMappingBo.setDocumentId(bo.getDocumentId());
        problemParagraphMappingBo.setId(mappingId);
        problemParagraphMappingService.insertByBo(problemParagraphMappingBo);

        List<EmbedDocument> embedDocumentList = DocConvert.problemBo2EmbedDoc(List.of(bo));

        embedDocumentList.forEach(embedDocument -> embedDocument.setOptType(BusinessType.INSERT));

        mqProducer.sendSecureMsg(RocketMQConstants.DOC_EMBED_TOPIC, embedDocumentList, UUID.randomUUID().toString(), true);

        return problemMapper.insert(MapstructUtils.convert(bo, Problem.class)) > 0;
    }

    private Boolean addProblemList(List<String> problemContentList, Long paragraphId, Long datasetId, Long documentId) {
        List<ProblemBo> problemList = problemContentList.stream().map(item -> {
            ProblemBo problem = new ProblemBo();
            problem.setContent(item);
            problem.setId(IdWorker.getId());
            problem.setDatasetId(datasetId);
            problem.setParagraphId(paragraphId);
            problem.setMappingId(IdWorker.getId());
            return problem;
        }).collect(Collectors.toList());

        List<ProblemParagraphMapping> paragraphMappings = problemList.stream().map(problem -> {
            ProblemParagraphMapping mapping = new ProblemParagraphMapping();
            mapping.setDocumentId(documentId);
            mapping.setProblemId(problem.getId());
            mapping.setParagraphId(paragraphId);
            mapping.setDatasetId(datasetId);
            mapping.setId(problem.getMappingId());
            return mapping;
        }).collect(Collectors.toList());


        EmbeddingUtil.getVectorStoreByDatasetId(datasetId)
                .add(DocConvert.convertDocuments(DocConvert.problemBo2EmbedDoc(problemList)));

        problemParagraphMappingService.insertBatch(paragraphMappings);

        return problemMapper.insertBatch(problemList.stream().map(item -> MapstructUtils.convert(item, Problem.class)).collect(Collectors.toList()));
    }

    @Override
    public Boolean insertBatch(List<Paragraph> paragraphs) {
        return baseMapper.insertBatch(paragraphs);
    }

    @Override
    @Transactional
    public Boolean deleteByDocId(Collection<Long> docIds) {
        List<Paragraph> paragraphs = getByDocIds(docIds);
        if (CollUtil.isEmpty(paragraphs)) {
            return true;
        }
        return deleteWithValidByIds(paragraphs.stream().map(Paragraph::getId).collect(Collectors.toList()), true);
    }

    @Override
    public List<ParagraphVo> selectVoBatchIds(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return baseMapper.selectVoByIds(ids);
    }


    private List<Paragraph> getByDocIds(Collection<Long> docIds) {
        if (CollUtil.isEmpty(docIds)) {
            return new ArrayList<>();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<Paragraph>()
                .in(Paragraph::getDocumentId, docIds));
    }
}
