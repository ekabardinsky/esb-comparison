package ru.ekabardinsky.magister.commons;

import ru.ekabardinsky.magister.commons.Monitoring.ResourcesUsageMonitor;
import ru.ekabardinsky.magister.commons.Monitoring.State;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ekabardinsky on 3/26/17.
 */
public class TestProperties {
    public static void main(String... ars) throws Exception {
        Thread.sleep(1000);
        ResourcesUsageMonitor resourcesUsageMonitor = new ResourcesUsageMonitor(10);
        resourcesUsageMonitor.start();
        Thread.sleep(1100);
        resourcesUsageMonitor.stop();
        List<State> result = resourcesUsageMonitor.getResult();

        result.forEach(x -> {
            System.out.println(x.getSystemCpuLoad());
        });
        long max = result.stream().max(Comparator.comparingLong(x->x.getFreePhysicalMemorySize())).get().getFreePhysicalMemorySize();
        long min = result.stream().min(Comparator.comparingLong(x->x.getFreePhysicalMemorySize())).get().getFreePhysicalMemorySize();
        System.out.println(max - min);
    }
}
