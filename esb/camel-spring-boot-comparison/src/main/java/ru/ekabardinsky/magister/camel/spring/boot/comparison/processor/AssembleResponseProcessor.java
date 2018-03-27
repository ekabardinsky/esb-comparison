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

        long applicationUseMemory = result.get(0).getApplicationUseMemory();
        long systemUseMemory = result.get(0).getSystemUseMemory();
        long freeMemory = result.get(0).getFreeMemory();
        long swapUseMemory = result.get(0).getSwapUseMemory();
        double applicationCpuLoad = result.get(0).getApplicationCpuLoad();
        double systemCpuLoad = result.get(0).getSystemCpuLoad();
        int concurrentlyMonitoringCount = result.get(0).getConcurrentlyMonitoringCount();

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
