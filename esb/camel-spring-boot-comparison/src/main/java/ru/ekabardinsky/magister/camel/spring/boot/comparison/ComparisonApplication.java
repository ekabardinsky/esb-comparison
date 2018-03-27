package ru.ekabardinsky.magister.camel.spring.boot.comparison;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.ekabardinsky.magister.camel.spring.boot.comparison.processor.AssembleResponseProcessor;
import ru.ekabardinsky.magister.camel.spring.boot.comparison.processor.JdbcAssembleQueryProcessor;
import ru.ekabardinsky.magister.camel.spring.boot.comparison.processor.MonitorCreationProcessor;
import ru.ekabardinsky.magister.camel.spring.boot.comparison.processor.TestCasesPropertiesProcessor;
import ru.ekabardinsky.magister.commons.Monitoring.ResourcesUsageMonitor;

@SpringBootApplication
public class ComparisonApplication {
    @Value("${soap.serviceAddress}")
    private String soapServiceAddress;

    @Value("${monitoring.sleep}")
    private Long monitoringSleep;

    @Value("${monitoring.bufferSize}")
    private Integer monitoringBufferSize;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Value("${db.url}")
    private String url;

    @Value("${db.driverClassName}")
    private String driverClassName;

    @Bean
    public Gson getGson() {
        return new Gson();
    }

    @Bean
    public TestCasesPropertiesProcessor testCasesPropertiesProcessor() {
        return new TestCasesPropertiesProcessor();
    }

    @Bean
    public ResourcesUsageMonitor resourcesUsageMonitor() {
        ResourcesUsageMonitor resourcesUsageMonitor = new ResourcesUsageMonitor(monitoringSleep, monitoringBufferSize);
        try {
            resourcesUsageMonitor.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourcesUsageMonitor;
    }

    @Bean
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(url, username, password);
        dataSource.setDriverClassName(driverClassName);

        return dataSource;
    }

    @Bean
    public JdbcAssembleQueryProcessor jdbcAssembleQueryProcessor() {
        return new JdbcAssembleQueryProcessor();
    }

    @Bean
    public MonitorCreationProcessor monitorCreationProcessor() {
        return new MonitorCreationProcessor();
    }

    @Bean
    public AssembleResponseProcessor assembleResponseProcessor() {
        return new AssembleResponseProcessor();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ComparisonApplication.class, args);
    }
}
