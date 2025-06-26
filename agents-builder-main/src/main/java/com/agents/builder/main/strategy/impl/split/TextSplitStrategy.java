package com.agents.builder.main.strategy.impl.split;

import com.agents.builder.main.domain.dto.SplitDto;
import com.agents.builder.main.domain.vo.DocumentVo;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.enums.DocType;
import com.agents.builder.main.spliter.CustomizeSplitter;
import com.agents.builder.main.strategy.common.DocSplitCommon;
import org.springframework.ai.reader.TextReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TextSplitStrategy extends DocSplitCommon {


    @Override
    public DocumentVo smartSplit(MultipartFile file) {
        TextReader textReader = new TextReader(file.getResource());
        return splitMdDocuments(file.getOriginalFilename(), textReader.get());
    }




    @Override
    public DocumentVo split(MultipartFile file,SplitDto dto) {
        CustomizeSplitter customizeSplitter = new CustomizeSplitter(dto.getPatterns(),dto.getLimit(),dto.getWithFilter());
        TextReader textReader = new TextReader(file.getResource());
        List<String> blockList = customizeSplitter.splitText(textReader.read().get(0).getText());

        List<ParagraphVo> paragraphVoList = blockList.stream().map(block -> {
            ParagraphVo paragraphVo = new ParagraphVo();
            paragraphVo.setContent(block);
            paragraphVo.setTitle(getTitle(block));
            return paragraphVo;
        }).collect(Collectors.toList());

        DocumentVo documentVo = new DocumentVo();
        documentVo.setName(file.getOriginalFilename());
        documentVo.setContent(paragraphVoList);
        return documentVo;
    }

    @Override
    public Set<DocType> docTypes() {
        return Set.of(DocType.TXT,DocType.MD);
    }
}
