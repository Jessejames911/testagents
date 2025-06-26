package com.agents.builder.main.consumer;


import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.core.enums.EntityStatus;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.mail.utils.MailUtils;
import com.agents.builder.common.rocketMQ.config.RocketMQConstants;
import com.agents.builder.common.web.enums.BusinessType;
import com.agents.builder.main.domain.EmbedDocument;
import com.agents.builder.main.domain.User;
import com.agents.builder.main.mapper.DocumentMapper;
import com.agents.builder.main.mapper.UserMapper;
import com.agents.builder.main.util.DocConvert;
import com.agents.builder.main.util.EmbeddingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
@RocketMQMessageListener(consumeMode = ConsumeMode.ORDERLY, topic = RocketMQConstants.DOC_EMBED_TOPIC, consumerGroup = RocketMQConstants.DOC_EMBED_TOPIC)
public class DocEmbeddingConsumer implements RocketMQListener<List<EmbedDocument>>, RocketMQPushConsumerLifecycleListener {

    private final DocumentMapper documentMapper;

    private final UserMapper userMapper;

    @Override
    public void onMessage(List<EmbedDocument> bizDocuments) {
        if (CollectionUtils.isEmpty(bizDocuments)) return;
        int failCount = 0;
        while (failCount < 3) {
            try {
                consume(bizDocuments);
                break;
            } catch (Exception e) {
                log.error("向量库更新失败", e);
                failCount++;
            }
        }
        if (failCount >= 3) {
            failHandle(bizDocuments);
        }
    }

    private void failHandle(List<EmbedDocument> bizDocuments) {
        BusinessType optType = bizDocuments.get(0).getOptType();
        if (BusinessType.INSERT.equals(optType)) {
            updateDocStatus(bizDocuments, EntityStatus.DISABLE.getCode().toString());
        }
        // 发送邮件
        Long userId = bizDocuments.get(0).getUserId();
        User user = userMapper.selectById(userId);
        String content = String.format("%s 您好，您%s的文档片段向量更新失败，详情：%s", user.getNickName(), optType.getDesc(), bizDocuments.stream().map(EmbedDocument::getTitle).collect(Collectors.toList()));
        MailUtils.send(user.getEmail(), "知识库文档向量更新失败", content, false);
    }

    private void updateDocStatus(List<EmbedDocument> bizDocuments, String status) {
        List<com.agents.builder.main.domain.Document> docList = bizDocuments.stream().map(item -> {
            com.agents.builder.main.domain.Document doc = new com.agents.builder.main.domain.Document();
            doc.setId(item.getDocumentId());
            doc.setStatus(status);
            return doc;
        }).collect(Collectors.toList());
        documentMapper.updateBatchById(CollUtil.distinct(docList, com.agents.builder.main.domain.Document::getId, false));
    }

    private void consume(List<EmbedDocument> bizDocuments) {
        log.info("监听到文档向量化消息: COUNT:{} DETAILS: {} =================》", bizDocuments.size(), bizDocuments.stream().map(EmbedDocument::getEmbedId).collect(Collectors.toList()));
        BusinessType optType = bizDocuments.get(0).getOptType();
        Long datasetId = bizDocuments.get(0).getDatasetId();

        VectorStore vectorStore = EmbeddingUtil.getVectorStoreByDatasetId(datasetId);

        switch (optType) {
            case INSERT:
                log.info("添加文档");
                List<Document> documents = DocConvert.convertDocuments(bizDocuments);
                vectorStore.add(documents);
                break;
            case UPDATE:
                log.info("更新文档");
                List<String> documentIds = bizDocuments.stream().map(item -> item.getEmbedId()).collect(Collectors.toList());
                vectorStore.delete(documentIds);
                List<Document> documents1 = DocConvert.convertDocuments(bizDocuments);
                vectorStore.add(documents1);
                break;
            case DELETE:
                log.info("删除文档");
                List<String> documentIds1 = bizDocuments.stream().map(item -> item.getEmbedId()).collect(Collectors.toList());
                vectorStore.delete(documentIds1);
                break;
            default:
                log.error("向量化文档未知操作");
                return;
        }
        log.info("向量库更新完成===========》");
        successHandle(bizDocuments);
    }

    private void successHandle(List<EmbedDocument> bizDocuments) {
        BusinessType optType = bizDocuments.get(0).getOptType();
        if (BusinessType.INSERT.equals(optType)) {
            updateDocStatus(bizDocuments, EntityStatus.ENABLE.getCode().toString());
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        // 设置最大重试次数
        consumer.setMaxReconsumeTimes(3);
    }
}
