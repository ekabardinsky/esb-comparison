package ru.ekabardinsky.magister.test.client.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.ekabardinsky.magister.test.client.application.scheduler.ResultSender;
import ru.ekabardinsky.magister.test.client.application.scheduler.TestCaseRunner;

/**
 * Created by ekabardinsky on 4/3/17.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class TestClientApplication extends SpringBootServletInitializer {

    @Bean
    public TestCaseRunner testCaseRunner() {
        return new TestCaseRunner();
    }

    @Bean
    public ResultSender resultSender() {
        return new ResultSender();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TestClientApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestClientApplication.class, args);
    }

}