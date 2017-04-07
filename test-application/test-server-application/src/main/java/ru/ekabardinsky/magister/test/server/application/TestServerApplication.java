package ru.ekabardinsky.magister.test.server.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.ekabardinsky.magister.commons.utils.HttpClient;
import ru.ekabardinsky.magister.test.server.application.manager.ClientManager;
import ru.ekabardinsky.magister.test.server.application.manager.EsbManager;
import ru.ekabardinsky.magister.test.server.application.manager.ResultManager;
import ru.ekabardinsky.magister.test.server.application.manager.ScheduleManager;

/**
 * Created by ekabardinsky on 4/3/17.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class TestServerApplication extends SpringBootServletInitializer {

    @Bean
    public HttpClient httpClient() {
        return new HttpClient();
    }
    @Bean
    public EsbManager esbManager() {
        return new EsbManager();
    }

    @Bean
    public ClientManager clientManager() {
        return new ClientManager();
    }

    @Bean
    public ResultManager resultManager() {
        return new ResultManager();
    }

    @Bean
    public ScheduleManager scheduleManager() {
        return new ScheduleManager();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TestServerApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestServerApplication.class, args);
    }

}