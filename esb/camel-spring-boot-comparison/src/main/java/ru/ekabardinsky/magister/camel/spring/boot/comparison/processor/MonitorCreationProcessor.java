package ru.ekabardinsky.magister.camel.spring.boot.comparison.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Value;
import ru.ekabardinsky.magister.commons.Monitoring.ResourcesUsageMonitor;

/**
 * Created by ekabardinsky on 23.03.2018.
 */
public class MonitorCreationProcessor implements Processor {
    @Value("${monitoring.sleep}")
    private Long monitoringSleep;

    @Value("${monitoring.bufferSize}")
    private Integer monitoringBufferSize;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResourcesUsageMonitor resourcesUsageMonitor = new ResourcesUsageMonitor(monitoringSleep, monitoringBufferSize);
        try {
            resourcesUsageMonitor.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        exchange.setProperty("monitor", resourcesUsageMonitor);
        resourcesUsageMonitor.start();
        exchange.getOut().setBody(exchange.getIn().getBody());
    }
}
