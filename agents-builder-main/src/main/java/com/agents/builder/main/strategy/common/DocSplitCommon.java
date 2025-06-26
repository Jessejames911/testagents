package com.agents.builder.main.strategy.common;

import cn.hutool.core.util.StrUtil;
import com.agents.builder.main.constants.DocMetaData;
import com.agents.builder.main.domain.vo.DocumentVo;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.service.IModelService;
import com.agents.builder.main.spliter.MdTreeSplitter;
import com.agents.builder.main.strategy.DocSplitStrategy;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class DocSplitCommon implements DocSplitStrategy {



    private static final String markdownItPrompt = """
            你是一个文本格式优化助手，精通markdown语法，并且熟悉文本格式的优化。
            请你将下面文本使用markdown语法进行优化，只需要回答优化后的结果不需要解释。
            待优化的文本：
            ---
            {un_markdown_text}
            ---
            """;

    private static final String UN_MD_TEXT_PLACEHOLDER = "un_markdown_text";

    protected List<ParagraphVo> document2ParagraphVo(List<Document> documentList) {
        return documentList.stream().map(document -> {
            ParagraphVo paragraphVo = new ParagraphVo();
            paragraphVo.setContent(document.getText());
            paragraphVo.setTitle((String) document.getMetadata().get(DocMetaData.METADATA_TITLE));
            return paragraphVo;
        }).collect(Collectors.toList());
    }

    public String getTitle(String content) {
        for (String s : content.split("\n")) {
            if (StrUtil.isNotBlank(s)) {
                return s;
            }
        }
        return null;
    }

    @NotNull
    protected DocumentVo setDocBaseInfo(MultipartFile file, List<Document> documentList) {
        documentList.forEach(document -> document.getMetadata().put(DocMetaData.METADATA_TITLE,getTitle(document.getText())));
        DocumentVo documentVo = new DocumentVo();
        documentVo.setName(file.getOriginalFilename());
        documentVo.setContent(document2ParagraphVo(documentList));
        return documentVo;
    }

    @NotNull
    protected DocumentVo splitMdDocuments(String fileName, List<Document> documents) {
        MdTreeSplitter treeSplitter = new MdTreeSplitter();
        List<Document> documentList = treeSplitter.split(documents);
        documentList.forEach(document -> document.getMetadata().put(DocMetaData.METADATA_TITLE,getTitle(document.getText())));
        DocumentVo documentVo = new DocumentVo();
        documentVo.setName(fileName);
        documentVo.setContent(document2ParagraphVo(documentList));
        return documentVo;
    }


}
