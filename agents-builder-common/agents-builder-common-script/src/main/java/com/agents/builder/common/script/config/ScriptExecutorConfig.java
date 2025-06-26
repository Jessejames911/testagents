package com.agents.builder.common.script.config;

import com.agents.builder.common.core.config.properties.SystemConfigResourceProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@AutoConfiguration
@RequiredArgsConstructor
public class ScriptExecutorConfig {


    private final SystemConfigResourceProperties systemConfigResourceProperties;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        Properties preprops = System.getProperties();
        props.put("python.home", systemConfigResourceProperties.getJythonPath());
        PythonInterpreter.initialize(preprops, props, new String[]{});
    }

}
