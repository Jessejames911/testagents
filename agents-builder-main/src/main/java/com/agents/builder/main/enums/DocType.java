package com.agents.builder.main.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DocType {

    PDF("pdf"),

    DOC("doc"),

    DOCX("docx"),

    PPT("ppt"),

    PPTX("pptx"),

    XLS("xls"),

    XLSX("xlsx"),

    TXT("txt"),

    MD("md"),

    HTML("html"),

    CSV("csv");

    private String type;

    public static DocType getByType(String type) {
        for (DocType docType : DocType.values()) {
            if (docType.getType().equals(type)) {
                return docType;
            }
        }
        return null;
    }
}
