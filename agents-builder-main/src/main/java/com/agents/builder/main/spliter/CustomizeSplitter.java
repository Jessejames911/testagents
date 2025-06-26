package com.agents.builder.main.spliter;

import cn.hutool.core.collection.CollUtil;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class CustomizeSplitter extends TokenTextSplitter {

    public static final Integer DEFAULT_SEG_LENGTH = 500;

    public static final Integer MIN_SEG_LENGTH = 350;

    public static final Integer MAX_SEG_LENGTH = 2048;

    public static final Integer MIN_EMBED_LENGTH = 30;


    private final List<String> splitPattern;

    private final Integer maxLength;

    private final boolean autoClean;


    public CustomizeSplitter(List<String> splitPattern, Integer maxLength, boolean autoClean) {
        super(maxLength, maxLength, MIN_EMBED_LENGTH, maxLength, true);
        this.splitPattern = splitPattern;
        this.maxLength = maxLength;
        this.autoClean = autoClean;
    }

    public CustomizeSplitter() {
        this(List.of(), DEFAULT_SEG_LENGTH, false);
    }


    @Override
    public List<String> splitText(String text) {
        if (CollUtil.isNotEmpty(splitPattern)) {
            return splitByPattern(text);
        }
        return super.splitText(text);
    }

    public List<String> splitByPattern(String text) {
        List<String> segments = new ArrayList<>();

        List<String> blocks = new CopyOnWriteArrayList<>();
        blocks.add(text);
        for (String pattern : splitPattern) {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
            for (String block : blocks) {
                Arrays.asList(block.split(pattern)).forEach(item->list.add(item));
            }
            blocks = list;
        }
        for (String block : blocks) {
            segments.addAll(super.splitText(block));
        }
        return segments;
    }
}
