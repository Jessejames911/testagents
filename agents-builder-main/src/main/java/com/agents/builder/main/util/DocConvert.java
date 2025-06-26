package com.agents.builder.main.util;

import cn.hutool.core.bean.BeanUtil;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.agents.builder.main.constants.DocMetaData;
import com.agents.builder.main.domain.EmbedDocument;
import com.agents.builder.main.domain.Paragraph;
import com.agents.builder.main.domain.Problem;
import com.agents.builder.main.domain.bo.ProblemBo;
import org.springframework.ai.document.Document;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.agents.builder.main.constants.DocMetaData.*;
import static org.springframework.ai.reader.TextReader.CHARSET_METADATA;

public class DocConvert {

    public static List<Document> convertDocuments(List<EmbedDocument> paragraphList) {
        return paragraphList.stream().map(bizDocument -> {

            Map<String, Object> metaData = buildMetaData(bizDocument);

            metaData.put(DocMetaData.METADATA_PROBLEM_ID, bizDocument.getProblemId());

            String content = bizDocument.getContent();

            return new Document(bizDocument.getEmbedId(), Optional.ofNullable(content).orElse(bizDocument.getTitle()), metaData);
        }).collect(Collectors.toList());
    }



    private static Map<String, Object> buildMetaData(EmbedDocument bizDocument) {

        Map<String, Object> customMetadata = getMetadata();

        customMetadata.put(METADATA_TITLE, bizDocument.getTitle());

        if (bizDocument.getDocumentId()!=null) {
            customMetadata.put(METADATA_PARENT_DOC_ID, bizDocument.getDocumentId().toString());
        }

        customMetadata.put(METADATA_PARAGRAPH_ID, bizDocument.getParagraphId().toString());

        customMetadata.put(METADATA_DATASET_ID, bizDocument.getDatasetId().toString());

        customMetadata.put(METADATA_DOC_NAME, bizDocument.getTitle());

        customMetadata.put(METADATA_ACTIVE, Optional.ofNullable(bizDocument.getIsActive()).orElse(true));

        return customMetadata;
    }

    private static Map<String, Object> getMetadata() {
        Map<String, Object> customMetadata = new HashMap<>();

        customMetadata.put(CHARSET_METADATA, StandardCharsets.UTF_8);

        customMetadata.put(METADATA_TIME, new Date());

        return customMetadata;
    }

    public static List<EmbedDocument> paragraph2EmbedDoc(List<Paragraph> paragraphList) {
        return paragraphList.stream().map(paragraph -> {
            EmbedDocument embedDocument = BeanUtil.toBean(paragraph, EmbedDocument.class);
            embedDocument.setParagraphId(paragraph.getId());
            embedDocument.setEmbedId(paragraph.getId().toString());
            embedDocument.setUserId(LoginHelper.getUserId());
            if (paragraph.getIsActive() == null){
                embedDocument.setIsActive(true);
            }
            return embedDocument;
        }).collect(Collectors.toList());
    }


    public static List<EmbedDocument> problemBo2EmbedDoc(List<ProblemBo> problemList) {
        return problemList.stream().map(problem->{
            EmbedDocument embedDocument = BeanUtil.toBean(problem, EmbedDocument.class);
            embedDocument.setEmbedId(problem.getMappingId().toString());
            embedDocument.setTitle(problem.getContent());
            embedDocument.setIsActive(true);
            embedDocument.setProblemId(problem.getId());
            embedDocument.setUserId(LoginHelper.getUserId());
            return embedDocument;
        }).collect(Collectors.toList());
    }


    public static List<EmbedDocument> problem2EmbedDoc(List<Problem> problemList) {
        return problemList.stream().map(problem->{
            EmbedDocument embedDocument = BeanUtil.toBean(problem, EmbedDocument.class);
            embedDocument.setEmbedId(problem.getMappingId().toString());
            embedDocument.setTitle(problem.getContent());
            embedDocument.setIsActive(true);
            embedDocument.setProblemId(problem.getId());
            embedDocument.setUserId(LoginHelper.getUserId());
            return embedDocument;
        }).collect(Collectors.toList());
    }
}
