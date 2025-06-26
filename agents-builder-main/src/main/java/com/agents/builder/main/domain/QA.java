package com.agents.builder.main.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class QA {

    @ExcelProperty(index = 0)
    private String title;

    @ExcelProperty(index = 1)
    private String content;

    @ExcelProperty(index = 2)
    private String questions;

    private Long id;
}
