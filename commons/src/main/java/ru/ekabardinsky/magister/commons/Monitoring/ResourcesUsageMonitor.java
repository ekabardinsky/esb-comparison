package ru.ekabardinsky.magister.commons.Monitoring;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ekabardinsky on 3/27/17.
 */
public class ResourcesUsageMonitor {
    private OperatingSystemMXBean operatingSystemMXBean;
    private List<State> stateList;
    private HashMap<String, Method> resourceGetters;
    private boolean isMonitoring;
    private long sleepInterval;
    private Exception monitoringException;
    private Thread monitorThread;

    public ResourcesUsageMonitor(long sleepInterval) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        this.sleepInterval = sleepInterval;
        initResourceGetters();
    }

    private void initResourceGetters() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.resourceGetters = new HashMap<>();
        for (Method method : State.class.getMethods()) {
            String methodName = method.getName();
            if (method.getParameterCount() == 0 && methodName.substring(0, 3).equals("get")) {
                Method resourceGetter = operatingSystemMXBean.getClass().getMethod(methodName);
                resourceGetter.setAccessible(true);
                resourceGetters.put(methodName, resourceGetter);
            }
        }
    }

    public void start() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        if (isMonitoring) {
            throw new IllegalStateException("Start monitoring before stop previous monitoring process");
        }
        isMonitoring = true;
        stateList = new ArrayList<>();
        Runnable monitoring = () -> {
            while (isMonitoring) {

                try {
                    Thread.sleep(sleepInterval);
                    stateList.add(getState());
                } catch (Exception e) {
                    isMonitoring = false;
                    monitoringException = e;
                    e.printStackTrace();
                }
            }
        };
        monitorThread = new Thread(monitoring);
        monitorThread.start();
    }

    public void stop() {
        isMonitoring = false;
    }

    public List<State> getResult() throws Exception {
        if (monitoringException != null) {
            throw monitoringException;
        }

        if (monitorThread.isAlive()) {
            Thread.sleep(2 * sleepInterval);
        }

        return stateList;
    }

    private State getState() throws InvocationTargetException, IllegalAccessException {
        State state = new State();
        state.setCommittedVirtualMemorySize((long) getResource("getCommittedVirtualMemorySize"));
        state.setFreePhysicalMemorySize((long) getResource("getFreePhysicalMemorySize"));
        state.setFreeSwapSpaceSize((long) getResource("getFreeSwapSpaceSize"));
        state.setProcessCpuLoad((double) getResource("getProcessCpuLoad"));
        state.setSystemCpuLoad((double) getResource("getSystemCpuLoad"));
        return state;
    }

    private Object getResource(String resourceGetterName) throws InvocationTargetException, IllegalAccessException {
        Method method = resourceGetters.get(resourceGetterName);
        Object invoke = method.invoke(operatingSystemMXBean);
        return invoke;
    }

    public Thread getMonitorThread() {
        return this.monitorThread;
    }
}
