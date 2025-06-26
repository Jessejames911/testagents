package com.agents.builder.common.script.impl;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.agents.builder.common.script.common.CommonScriptExecutor;
import com.agents.builder.common.script.enums.ScriptLanguage;
import lombok.RequiredArgsConstructor;
import org.python.core.PyCode;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PyScriptExecutorImpl extends CommonScriptExecutor {

    private final String RESULT_KEY = "result";

    @Override
    public ScriptLanguage type() {
        return ScriptLanguage.PYTHON;
    }

    @Override
    public Object execute(String script, Map<String, Object> params) {
        PythonInterpreter pythonInterpreter = getPythonInterpreter();
        pythonInterpreter.compile(convertScript(script));

        params.forEach(pythonInterpreter::set);
        pythonInterpreter.exec(script);

        PyObject result = pythonInterpreter.get(RESULT_KEY);

        if (result == null) {
            return null;
        }

        pythonInterpreter.close();

        return result.__tojava__(Object.class);
    }

    @Override
    public PyCode compile(String script) {
        return null;
    }

    private String convertScript(String script) {
        String[] lineArray = script.split("\\n");
        List<String> noBlankLineList = Arrays.stream(lineArray)
                .filter(s -> !StrUtil.isBlank(s))
                .collect(Collectors.toList());

        // 用第一行的缩进的空格数作为整个代码的缩进量
        String blankStr = ReUtil.getGroup0("^[ ]*", noBlankLineList.get(0));

        // 重新构建脚本
        StringBuilder scriptSB = new StringBuilder();
        noBlankLineList.forEach(s -> scriptSB.append(StrUtil.format("{}\n", s.replaceFirst(blankStr, StrUtil.EMPTY))));

        return scriptSB.toString().replace("return", RESULT_KEY + "=");
    }

    public PythonInterpreter getPythonInterpreter() {
        PySystemState systemState = new PySystemState();
        systemState.setdefaultencoding("UTF-8");
        return new PythonInterpreter(null, systemState);
    }
}
