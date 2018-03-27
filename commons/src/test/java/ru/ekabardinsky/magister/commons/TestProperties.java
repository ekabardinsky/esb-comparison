package ru.ekabardinsky.magister.commons;

import ru.ekabardinsky.magister.commons.Monitoring.ResourcesUsageMonitor;
import ru.ekabardinsky.magister.commons.Monitoring.State;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by ekabardinsky on 3/26/17.
 */
public class TestProperties {
    public static void main(String... ars) throws Exception {
        for( int i = 0 ; i < 10; i++) {
            new Thread(() -> {
                ResourcesUsageMonitor resourcesUsageMonitor = new ResourcesUsageMonitor(10, 10000);
                try {
                    resourcesUsageMonitor.initialize();
                    printStates(resourcesUsageMonitor);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();
        }
    }

    public static void printStates(ResourcesUsageMonitor resourcesUsageMonitor) throws Exception {
        resourcesUsageMonitor.start();

        Thread.sleep(1000);

        resourcesUsageMonitor.stop();
        List<State> result = resourcesUsageMonitor.getResult();

        result.forEach(System.out::println);

        System.out.println("----------------Created states: " + result.size() + " ----------");

    }
}
