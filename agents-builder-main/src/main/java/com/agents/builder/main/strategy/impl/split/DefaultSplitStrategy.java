package com.agents.builder.main.strategy.impl.split;

import com.agents.builder.main.domain.dto.SplitDto;
import com.agents.builder.main.domain.vo.DocumentVo;
import com.agents.builder.main.enums.DocType;
import com.agents.builder.main.spliter.CustomizeSplitter;
import com.agents.builder.main.strategy.common.DocSplitCommon;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DefaultSplitStrategy extends DocSplitCommon {
    @Override
    public DocumentVo smartSplit(MultipartFile file) {
        TikaDocumentReader reader = new TikaDocumentReader(file.getResource());
        List<Document> mdDocuments = convert2Md(reader.get());
        return splitMdDocuments(file.getOriginalFilename(), mdDocuments);
    }

    @Override
    public DocumentVo split(MultipartFile file, SplitDto dto) {
        TikaDocumentReader reader = new TikaDocumentReader(file.getResource());

        if (dto.getPatterns() == null || dto.getPatterns().isEmpty()) {
            return smartSplit(file);
        }
        CustomizeSplitter textSplitter = new CustomizeSplitter(dto.getPatterns(), dto.getLimit(), dto.getWithFilter());
        List<Document> documentList = textSplitter.split(reader.get());
        return setDocBaseInfo(file, documentList);
    }

    private List<Document> convert2Md(List<Document> documents) {
        return documents.stream().map(document -> {
            String content = document.getText();
            //将每一行分成string字段
            String[] split = content.split("\\r\\n");

            StringBuilder result = new StringBuilder();
            int status = 1;
            //正则判断每一行是否首页字段
            for (int x = 0; x < split.length; x++) {
                String dl = "";
                if (Pattern.matches("^[0-9].[0-9].[0-9].[0-9][\\s\\S]*", split[x])) {
                    String[] spl = split[x].split("\\.");
                    dl = "#### " + spl[spl.length - 2] + "\\." + spl[spl.length - 1];
                } else if (Pattern.matches("^[0-9].[0-9].[0-9][\\s\\S]*", split[x])) {
                    String[] spl = split[x].split("\\.");
                    dl = "### " + spl[spl.length - 1];
                } else if (Pattern.matches("^[0-9].[0-9].[\\s\\S]*", split[x])) {
                    dl = "## " + split[x];
                } else if (Pattern.matches("^[0-9] [\\s\\S]*", split[x])) {
                    dl = "# " + split[x];
                } else {

                    //以：（开头，或者以）结尾，且汉字长度不超过10的行：
                    if (Pattern.matches("[\\s\\S]*[：]$|[\\s\\S]*[：] $|^（[\\s\\S]*|[\\s\\S]*[）]$|[\\s\\S]*[）] $", split[x]) && split[x].length() <= 30) {
                        split[x] = "\n" + "**" + split[x] + "**" + "\n";
                    } else if (Pattern.matches("[\\s\\S]*[：；]$|[\\s\\S]*[：；] $", split[x])) {
                        split[x] = split[x] + "\n\n";
                    }
                    dl = split[x];
                    status = 2;
                }


                //判断是否换行
                //status:2非标题， 正则表达式最后非以：。结尾
                if (status == 2 && !Pattern.matches("[\\s\\S]*[。：]$", dl)) {
                    result.append(dl);
                } else {
                    //以[#]开头[:,: ]结尾必须换行^（[\s\S]*|[\s\S]*[）]$
                    if (Pattern.matches("^#[\\s\\S]*", dl)) {
                        result.append("\r\n");
                        result.append(dl);
                        result.append("\r\n");
                    } else {
                        result.append(dl);
                        //换行
                        result.append("\r\n");
                        result.append("\n");
                    }
                }
                //重置参数
                status = 1;
            }
            return new Document(result.toString());
        }).collect(Collectors.toList());
    }

    @Override
    public Set<DocType> docTypes() {
        return Set.of(DocType.PDF, DocType.DOC, DocType.DOCX, DocType.HTML);
    }
}
