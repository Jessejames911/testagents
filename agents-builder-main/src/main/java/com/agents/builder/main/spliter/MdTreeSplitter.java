package com.agents.builder.main.spliter;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MdTreeSplitter extends TokenTextSplitter {


    static final Integer DEFAULT_LIMIT = 2048;

    static List<String> defaultPatternList = Arrays.asList(
            "(?<=)(?<!#)# (?!#).*|(?<=\\n)(?<!#)# (?!#).*",
            "(?<=)(?<!#)## (?!#).*|(?<=\\n)(?<!#)# (?!#).*",
            "(?<=)(?<!#)### (?!#).*|(?<=\\n)(?<!#)# (?!#).*",
            "(?<=)(?<!#)#### (?!#).*|(?<=\\n)(?<!#)# (?!#).*",
            "(?<=)(?<!#)##### (?!#).*|(?<=\\n)(?<!#)# (?!#).*",
            "(?<=)(?<!#)###### (?!#).*|(?<=\\n)(?<!#)# (?!#).*"
    );

    public MdTreeSplitter() {
        super(DEFAULT_LIMIT, DEFAULT_LIMIT/2, 10, DEFAULT_LIMIT, true);
    }


    @Override
    protected List<String> splitText(String text) {
        text = text.replace("\r\n", "\n").replace("\r", "\n").replace("\0", "");

        List<Map<String, Object>> firstTitleMapList = parseTitle(text, 0);
        List<Map<String, Object>> resultMap = new ArrayList<>();

        List<String> resultList = new ArrayList<>();

        parseTreeResult(resultMap, firstTitleMapList, "");

        for (Map<String, Object> map : resultMap) {
            String title = (String) map.get("title");
            String content = (String) map.get("content");
            resultList.add(title + "\n" + content);
        }

        return resultList;
    }

    private void parseTreeResult(List<Map<String, Object>> resultMap, List<Map<String, Object>> firstTitleMapList, String tmpTitle) {
        if (CollectionUtils.isEmpty(firstTitleMapList)) return;

        if (firstTitleMapList.size() == 1) {
            Map<String, Object> titleMap = firstTitleMapList.get(0);

            List<String> limitContentList = getFilterLimitContentList((String) titleMap.get("content"));

            if (!CollectionUtils.isEmpty(limitContentList)) {
                String title = filterSpecialChar(tmpTitle);
                for (String content : limitContentList) {
                    resultMap.add(Map.of("title", title, "content", content));
                }
            }

        }
        for (Map<String, Object> titleMap : firstTitleMapList) {
            String title = (String) titleMap.get("title");

            String content = (String) titleMap.get("content");
            Integer level = (Integer) titleMap.get("level");
            if (!StringUtils.hasText(title) || !StringUtils.hasText(content)) {
                continue;
            }

            parseTreeResult(resultMap, parseTitle(content, level), tmpTitle + title);
        }

    }

    private List<String> getFilterLimitContentList(String content) {
        if (content.length() < DEFAULT_LIMIT) {
            String filter = content.replace("\n", "").replace(" ", "");
            if (filter.length() <= 10 || (filter.indexOf("This document was") <= 20 && filter.indexOf("This document was") >= 0)) {
                return Collections.emptyList();
            }
            return List.of(content);
        }

        return super.splitText(content);
    }

    private String filterSpecialChar(String content) {
        Map<Pattern, String> replaceMap = new HashMap<>();
        replaceMap.put(Pattern.compile("\n+"), "\n");
        replaceMap.put(Pattern.compile(" +"), " ");
        replaceMap.put(Pattern.compile("#+"), "");
        replaceMap.put(Pattern.compile("\t+"), "");
        for (Map.Entry<Pattern, String> entry : replaceMap.entrySet()) {
            content = entry.getKey().matcher(content).replaceAll(entry.getValue());
        }
        return content;
    }

    private List<Map<String, Object>> parseTitle(String text, int index) {
        List<Map<String, Object>> result = parseLevel(text, index);
        // 如果当前级别标题未匹配到则一直匹配子标题，直到匹配到为止
        if (result.isEmpty() || result.size() == 1) {
            if (index == defaultPatternList.size() - 1) {
                return Collections.emptyList();
            }
            return parseLevel(text, index + 1);
        }
        return result;
    }

    private List<Map<String, Object>> parseLevel(String text, int patternIndex) {

        String pattern = defaultPatternList.get(patternIndex);
        List<Map<String, Object>> results = new ArrayList<>();
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(text);

        int lastIndex = 0;
        String lastTitle = "";
        while (matcher.find()) {

            int start = matcher.start();
            if (lastIndex == 0 && matcher.start() != 0) {
                Map<String, Object> result = new HashMap<>();
                result.put("title", "");
                result.put("content", text.substring(lastIndex, matcher.start()));
                result.put("level", patternIndex + 1);
                results.add(result);
                lastIndex = matcher.end();
                start = lastIndex;
            }
            Map<String, Object> result = new HashMap<>();
            result.put("title", lastTitle);
            result.put("content", text.substring(lastIndex, start));
            result.put("level", patternIndex + 1);
            results.add(result);
            lastIndex = matcher.end();
            lastTitle = matcher.group();
        }
        // 添加最后一段内容，如果没有匹配到模式，则整个文本作为最后一段的内容
        if (lastIndex < text.length()) {
            Map<String, Object> lastResult = new HashMap<>();
            lastResult.put("title", lastTitle);
            lastResult.put("content", text.substring(lastIndex));
            lastResult.put("level", patternIndex + 1);
            results.add(lastResult);
        }
        return results;
    }

}
