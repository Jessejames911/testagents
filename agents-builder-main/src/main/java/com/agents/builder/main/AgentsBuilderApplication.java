package com.agents.builder.main;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(exclude = {OpenAiAutoConfiguration.class})
@ComponentScan(basePackages = "com.agents.builder")
@MapperScan(basePackages = "com.agents.**.mapper")
@EnableScheduling
@EnableAsync
public class AgentsBuilderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentsBuilderApplication.class, args);
    }

}
