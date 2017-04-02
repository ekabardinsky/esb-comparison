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
        ResourcesUsageMonitor resourcesUsageMonitor = new ResourcesUsageMonitor(10, 10000);
        resourcesUsageMonitor.initialize();

        for( int i = 0 ; i < 10; i++) {
            printStates(resourcesUsageMonitor, 1000000);
        }
    }

    public static void printStates(ResourcesUsageMonitor resourcesUsageMonitor, int loopSize) throws Exception {
        resourcesUsageMonitor.start();

        List<State> stateList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < loopSize; i++) {
            State state = new State();
            state.setSystemTime(random.nextLong());
            state.setUsedMemory(random.nextLong());
            state.setSystemCpuLoad(random.nextDouble());
            stateList.add(state);
        }
        resourcesUsageMonitor.stop();
        List<State> result = resourcesUsageMonitor.getResult();

        State max = result.stream().max(Comparator.comparingLong(x -> x.getUsedMemory())).get();
        System.out.println(max);
        System.out.println("----------------Created states: " + result.size() + " ---- with " + stateList.size() + " fake object ----------");

    }
}
