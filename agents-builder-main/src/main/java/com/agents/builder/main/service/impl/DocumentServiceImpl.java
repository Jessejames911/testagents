package com.agents.builder.main.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.agents.builder.common.core.domain.model.KVObj;
import com.agents.builder.common.core.enums.EntityStatus;
import com.agents.builder.common.core.excel.utils.ExcelUtil;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.rocketMQ.config.RocketMQConstants;
import com.agents.builder.common.rocketMQ.service.MQProducer;
import com.agents.builder.common.web.enums.BusinessType;
import com.agents.builder.main.domain.*;
import com.agents.builder.main.domain.bo.DocumentBo;
import com.agents.builder.main.domain.bo.ParagraphBo;
import com.agents.builder.main.domain.dto.DocGenerateRelatedDto;
import com.agents.builder.main.domain.dto.SplitDto;
import com.agents.builder.main.domain.vo.DocumentVo;
import com.agents.builder.main.mapper.DocumentMapper;
import com.agents.builder.main.mapper.ProblemMapper;
import com.agents.builder.main.mapper.ProblemParagraphMappingMapper;
import com.agents.builder.main.service.IDocumentService;
import com.agents.builder.main.service.IParagraphService;
import com.agents.builder.main.strategy.DocSplitStrategy;
import com.agents.builder.main.strategy.context.DocSplitContext;
import com.agents.builder.main.util.DocConvert;
import com.agents.builder.main.util.DocUtil;
import com.agents.builder.main.util.EmbeddingUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
public class DocumentServiceImpl implements IDocumentService {

    private final DocumentMapper baseMapper;

    private final DocSplitContext docSplitContext;

    private final IParagraphService paragraphService;

    private final MQProducer mqProducer;

    private final ProblemMapper problemMapper;

    private final ProblemParagraphMappingMapper problemParagraphMapper;
    /**
     * 查询
     */
    @Override
    public DocumentVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<DocumentVo> queryPageList(DocumentBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Document> lqw = buildQueryWrapper(bo);
        Page<DocumentVo> result = baseMapper.getVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<DocumentVo> queryList(DocumentBo bo) {
        LambdaQueryWrapper<Document> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Document> buildQueryWrapper(DocumentBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Document> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), Document::getName, bo.getName());
        lqw.eq(bo.getCharLength() != null, Document::getCharLength, bo.getCharLength());
        lqw.eq(bo.getStatus()!=null, Document::getStatus, bo.getStatus());
        lqw.eq(bo.getIsActive() != null, Document::getIsActive, bo.getIsActive());
        lqw.eq(bo.getType()!=null, Document::getType, bo.getType());
        lqw.eq(bo.getDatasetId() != null, Document::getDatasetId, bo.getDatasetId());
        lqw.eq(StringUtils.isNotBlank(bo.getHitHandlingMethod()), Document::getHitHandlingMethod, bo.getHitHandlingMethod());
        lqw.eq(bo.getDirectlyReturnSimilarity() != null, Document::getDirectlyReturnSimilarity, bo.getDirectlyReturnSimilarity());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(DocumentBo bo) {
        Document add = MapstructUtils.convert(bo, Document.class);
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
    public DocumentVo updateByBo(DocumentBo bo) {
        Document update = MapstructUtils.convert(bo, Document.class);
        validEntityBeforeSave(update);
        baseMapper.updateById(update);
        if (bo.getIsActive()!=null){
            // 更新所有段落
            paragraphService.updateIsActiveByDocId(bo.getId(),bo.getIsActive());
        }
        return queryById(bo.getId());
    }

    @Override
    public Boolean batchHitHandling(DocumentBo bo) {
        Assert.notEmpty(bo.getIdList(),"文档id不能为空");
        return baseMapper.update(new LambdaUpdateWrapper<Document>()
                .set(Document::getHitHandlingMethod,bo.getHitHandlingMethod())
                .set(Document::getDirectlyReturnSimilarity,bo.getDirectlyReturnSimilarity())
                .in(Document::getId,bo.getIdList()))>0;
    }

    @Override
    public Boolean batchGenerateRelated(DocGenerateRelatedDto dto) {
        List<Paragraph> paragraphList = paragraphService.getByDocumentIds(dto.getDocumentIdList());
        Map<Long, List<Paragraph>> docParagraphMap = paragraphList.stream().collect(Collectors.groupingBy(Paragraph::getDocumentId));

        docParagraphMap.forEach((k,v)->{
            dto.setDocumentId(k);
            dto.setParagraphIdList(v.stream().map(Paragraph::getId).collect(Collectors.toList()));
            paragraphService.batchGenerateRelated(dto);
        });
        return true;
    }

    @Override
    public List<KVObj> getSplitPattern() {
        return List.of(new KVObj("#","#"),
                new KVObj("##","##"),
                new KVObj("###","###"),
                new KVObj("####","####"),
                new KVObj("#####","#####"),
                new KVObj("---","---"),
                new KVObj("空格","\\s+"),
                new KVObj("分号",";"),
                new KVObj("逗号",","),
                new KVObj("回车","\\n"),
                new KVObj("空行",System.lineSeparator()));
    }

    @Override
    @SneakyThrows
    @Transactional
    public List<DocumentVo> importTable(SplitDto dto) {
        List<Document> documentList = new ArrayList<>();
        List<Paragraph> paragraphList = new ArrayList<>();
        for (MultipartFile file : dto.getFile()) {
            Map<String, List<Map<String, Object>>> multiSheetResults = ExcelUtil.importExcelMultiSheetToMap(file.getInputStream());
            multiSheetResults.forEach((sheetName, rows) -> {
                long documentId = IdWorker.getId();
                paragraphList.addAll(tableRowToParagraph(rows, dto.getDatasetId(), documentId));

                documentList.add(getDocument(dto.getDatasetId(), file.getOriginalFilename()+"-"+Optional.ofNullable(sheetName).orElse(""), documentId));
            });
        }
        paragraphService.insertBatch(paragraphList);
        baseMapper.insertBatch(documentList);
        // 向量化
        List<EmbedDocument> embedDocumentList = DocConvert.paragraph2EmbedDoc(paragraphList);

        embedDocumentList.forEach(embedDocument -> embedDocument.setOptType(BusinessType.INSERT));

        mqProducer.sendSecureMsg(RocketMQConstants.DOC_EMBED_TOPIC, embedDocumentList, UUID.randomUUID().toString(), true);

        return documentList.stream().map(item-> MapstructUtils.convert(item, DocumentVo.class)).collect(Collectors.toList());
    }

    @NotNull
    private Document getDocument(Long datasetId, String fileName, long documentId) {
        Document document = new Document();
        document.setId(documentId);
        document.setDatasetId(datasetId);
        document.setName(fileName);
        return document;
    }

    @Override
    @SneakyThrows
    @Transactional
    public List<DocumentVo> importQa(SplitDto dto) {
        List<Document> documentList = new ArrayList<>();
        List<Paragraph> paragraphList = new ArrayList<>();
        List<Problem> problemList = new ArrayList<>();
        List<ProblemParagraphMapping> problemParagraphMappingList = new ArrayList<>();
        for (MultipartFile file : dto.getFile()) {
            Map<String, List<QA>> multiSheetResults = ExcelUtil.importExcelMultiSheet(file.getInputStream(), QA.class);
            multiSheetResults.forEach((sheetName, rows) -> {
                long documentId = IdWorker.getId();
                documentList.add(getDocument(dto.getDatasetId(), file.getOriginalFilename()+"-"+Optional.ofNullable(sheetName).orElse(""), documentId));
                paragraphList.addAll(qaRowToParagraph(rows, dto.getDatasetId(), documentId));
                problemList.addAll(qaRowToProblem(rows,dto.getDatasetId(),problemParagraphMappingList,documentId));
            });
        }
        paragraphService.insertBatch(paragraphList);
        baseMapper.insertBatch(documentList);
        problemMapper.insertBatch(problemList);
        problemParagraphMapper.insertBatch(problemParagraphMappingList);
        // 向量化分段
        List<EmbedDocument> embedDocumentList = DocConvert.paragraph2EmbedDoc(paragraphList);

        embedDocumentList.forEach(embedDocument -> embedDocument.setOptType(BusinessType.INSERT));

        mqProducer.sendSecureMsg(RocketMQConstants.DOC_EMBED_TOPIC, embedDocumentList, UUID.randomUUID().toString(), true);

        // 向量化关联问题
        List<EmbedDocument> embedProblemList = DocConvert.problem2EmbedDoc(problemList);
        embedProblemList.forEach(embedDocument -> embedDocument.setOptType(BusinessType.INSERT));
        mqProducer.sendSecureMsg(RocketMQConstants.DOC_EMBED_TOPIC, embedProblemList, UUID.randomUUID().toString(), true);

        return documentList.stream().map(item-> MapstructUtils.convert(item, DocumentVo.class)).collect(Collectors.toList());
    }

    private List<Problem> qaRowToProblem(List<QA> rows, Long datasetId,List<ProblemParagraphMapping> problemParagraphMappingList,Long documentId) {
        if (CollUtil.isEmpty(rows))return Collections.emptyList();
        return rows.stream().flatMap(row->{
            if (StrUtil.isNotBlank(row.getQuestions())){
                return Arrays.stream(row.getQuestions().split("\n")).map(question->{
                    long problemId = IdWorker.getId();
                    long mappingId = IdWorker.getId();
                    Problem problem = new Problem();
                    problem.setId(problemId);
                    problem.setDatasetId(datasetId);
                    problem.setContent(question.trim());
                    problem.setMappingId(mappingId);
                    problem.setParagraphId(row.getId());


                    ProblemParagraphMapping problemParagraphMapping = new ProblemParagraphMapping();
                    problemParagraphMapping.setId(mappingId);
                    problemParagraphMapping.setProblemId(problemId);
                    problemParagraphMapping.setParagraphId(row.getId());
                    problemParagraphMapping.setDatasetId(datasetId);
                    problemParagraphMapping.setDocumentId(documentId);
                    problemParagraphMappingList.add(problemParagraphMapping);
                    return problem;
                });
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<Paragraph> qaRowToParagraph(List<QA> rows, Long datasetId, long documentId) {
        if (CollUtil.isEmpty(rows))return Collections.emptyList();
        return rows.stream().map(row->{
            long paragraphId = IdWorker.getId();
            row.setId(paragraphId);
            Paragraph paragraph = new Paragraph();
            paragraph.setId(paragraphId);
            paragraph.setContent(row.getContent());
            paragraph.setDatasetId(datasetId);
            paragraph.setDocumentId(documentId);
            paragraph.setTitle(row.getTitle());
            return paragraph;
        }).collect(Collectors.toList());
    }

    private List<Paragraph> tableRowToParagraph(List<Map<String, Object>> rows, Long datasetId, Long documentId) {
        if (CollUtil.isEmpty(rows))return Collections.emptyList();
        return rows.stream().map(row->{
            Paragraph paragraph = new Paragraph();
            paragraph.setContent(DocUtil.mapToStr(row));
            paragraph.setDatasetId(datasetId);
            paragraph.setDocumentId(documentId);
            paragraph.setTitle("-");
            return paragraph;
        }).collect(Collectors.toList());
    }


    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Document entity){
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
        // 删除段落
        paragraphService.deleteByDocId(ids);


        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public List<DocumentVo> split(SplitDto dto) {
        return dto.getFile().stream().map(file -> {
            String suffix = FileUtil.getSuffix(file.getOriginalFilename());
            DocSplitStrategy strategy = docSplitContext.getBindingService(suffix, () -> new ServiceException("不支持的文件类型"));
            if (CollUtil.isNotEmpty(dto.getPatterns())){
                return strategy.split(file,dto);
            }
            return strategy.smartSplit(file);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean insertBatchByBo(List<DocumentBo> boList) {
        List<Document> documents = new ArrayList<>();

        List<ParagraphBo> paragraphs = new ArrayList<>();

        boList.forEach(bo -> {
            long docId = IdWorker.getId();
            Document document = MapstructUtils.convert(bo, Document.class);
            document.setId(docId);
            document.setStatus(EntityStatus.ENABLE.getCode().toString());
            if (CollUtil.isNotEmpty(bo.getParagraphs())){
                bo.getParagraphs().forEach(paragraphBo -> {
                    paragraphBo.setDocumentId(document.getId());
                    paragraphBo.setDatasetId(document.getDatasetId());
                    paragraphs.add(paragraphBo);
                });
                document.setStatus(EntityStatus.INIT.getCode().toString());
            }
            documents.add(document);
        });

        if (CollUtil.isNotEmpty(paragraphs)){
            paragraphService.insertBatchByBo(paragraphs);
        }
        return baseMapper.insertBatch(documents);
    }

    @Override
    @Transactional
    public Boolean deleteByDatasetId(Collection<Long> datasetIds) {
        List<Document> documents = getByByDatasetId(datasetIds);
        if (CollUtil.isEmpty(documents)){
            return true;
        }
        return deleteWithValidByIds(documents.stream().map(Document::getId).collect(Collectors.toList()), true);
    }

    @Override
    public Boolean refresh(Long id,Long datasetId) {
        return reEmbedding(List.of(id), datasetId);
    }

    @Override
    public Boolean reEmbeddingByDatasetId(Long datasetId) {
        List<Document> documentList = getByByDatasetId(List.of(datasetId));
        if (CollUtil.isEmpty(documentList)){
            return true;
        }
        return reEmbedding(documentList.stream().map(Document::getId).collect(Collectors.toList()),datasetId);
    }

    @Override
    public Boolean batchRefresh(List<Long> idList, Long datasetId) {
        return reEmbedding(idList,datasetId);
    }

    @Override
    @Transactional
    public Boolean migrate(List<Long> documentIdList, Long datasetId, Long target) {
        Assert.notNull(datasetId,"datasetId is null");
        Assert.notNull(target,"target is null");
        Assert.notEmpty(documentIdList,"documentIdList is null");

        List<Paragraph> paragraphList = paragraphService.getByDocumentIds(documentIdList);

        Map<Long, List<Paragraph>> docMap = paragraphList.stream().collect(Collectors.groupingBy(Paragraph::getDocumentId));

        docMap.forEach((k,v)->{
            List<Long> paragraphIdList = v.stream().map(Paragraph::getId).collect(Collectors.toList());

            paragraphService.migrate(paragraphIdList,datasetId,target,k,k);
        });

        return baseMapper.update(new LambdaUpdateWrapper<Document>()
                .set(Document::getDatasetId,target)
                .in(Document::getId,documentIdList)) > 0;
    }



    private Boolean reEmbedding(List<Long> documentIds,Long datasetId) {
        updateStatusByIds(documentIds, EntityStatus.WAITING.getCode().toString());

        List<Paragraph> paragraphList = paragraphService.getByDocumentIds(documentIds);

        if (CollUtil.isEmpty(paragraphList)){
            return true;
        }
        List<EmbedDocument> embedDocuments = paragraphService.getEmbedDocuments(paragraphList.stream().map(Paragraph::getId).collect(Collectors.toList()));
        VectorStore vectorStore = EmbeddingUtil.getVectorStoreByDatasetId(datasetId);

        vectorStore.delete(embedDocuments.stream().map(EmbedDocument::getEmbedId).collect(Collectors.toList()));

        vectorStore.add(DocConvert.convertDocuments(embedDocuments));

        return updateStatusByIds(documentIds, EntityStatus.ENABLE.getCode().toString());
    }

    private Boolean updateStatusByIds(List<Long> documentIds, String status) {
        if (CollUtil.isEmpty(documentIds)) return true;
        return baseMapper.update(new LambdaUpdateWrapper<Document>()
                .set(Document::getStatus,status)
                .in(Document::getId,documentIds))>0;
    }

    private List<Document> getByByDatasetId(Collection<Long> datasetIds) {
        if (CollUtil.isEmpty(datasetIds)){
            return Collections.emptyList();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<Document>()
                .in(Document::getDatasetId,datasetIds));
    }
}
