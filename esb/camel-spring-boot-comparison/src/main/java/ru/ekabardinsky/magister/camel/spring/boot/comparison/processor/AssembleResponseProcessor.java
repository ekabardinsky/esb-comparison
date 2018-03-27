package ru.ekabardinsky.magister.camel.spring.boot.comparison.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import ru.ekabardinsky.magister.commons.Monitoring.ResourcesUsageMonitor;
import ru.ekabardinsky.magister.commons.Monitoring.State;

import java.util.List;

/**
 * Created by ekabardinsky on 23.03.2018.
 */
public class AssembleResponseProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        ResourcesUsageMonitor monitor = (ResourcesUsageMonitor) exchange.getProperty("monitor");
        monitor.stop();

        List<State> result = monitor.getResult();

        long applicationUseMemory = result.stream().mapToLong(x->x.getApplicationUseMemory()).max().getAsLong();
        long systemUseMemory = result.stream().mapToLong(x->x.getSystemUseMemory()).max().getAsLong();
        long freeMemory = result.stream().mapToLong(x->x.getFreeMemory()).min().getAsLong();
        long swapUseMemory = result.stream().mapToLong(x->x.getSwapUseMemory()).max().getAsLong();
        double applicationCpuLoad = result.stream().mapToDouble(x -> x.getApplicationCpuLoad()).average().getAsDouble();
        double systemCpuLoad = result.stream().mapToDouble(x->x.getSystemCpuLoad()).average().getAsDouble();
        long concurrentlyMonitoringCount = result.stream().mapToLong(x->x.getConcurrentlyMonitoringCount()).max().getAsLong();

        exchange.getOut().setHeader("applicationUseMemory", applicationUseMemory);
        exchange.getOut().setHeader("systemUseMemory", systemUseMemory);
        exchange.getOut().setHeader("freeMemory", freeMemory);
        exchange.getOut().setHeader("swapUseMemory", swapUseMemory);
        exchange.getOut().setHeader("applicationCpuLoad", applicationCpuLoad);
        exchange.getOut().setHeader("systemCpuLoad", systemCpuLoad);
        exchange.getOut().setHeader("concurrentlyMonitoringCount", concurrentlyMonitoringCount);

        exchange.getOut().setBody(exchange.getIn().getBody());
    }
}
