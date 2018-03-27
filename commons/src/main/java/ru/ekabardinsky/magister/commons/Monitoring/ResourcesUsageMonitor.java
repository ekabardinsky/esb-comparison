package ru.ekabardinsky.magister.commons.Monitoring;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by ekabardinsky on 3/27/17.
 */
public class ResourcesUsageMonitor {
    private static int CURRENTLY_RUN_MONITORS = 0;

    //usage monitoring sources
    private OperatingSystemMXBean operatingSystemMXBean;
    private Runtime runtime;

    //state buffers
    private long[] applicationUseMemoryBuffer;
    private long[] systemUseMemoryBuffer;
    private long[] freeMemoryBuffer;
    private long[] swapMemoryBuffer;
    private double[] applicationCpuLoadBuffer;
    private double[] systemCpuLoadBuffer;
    private int[] concurrentlyMonitoringCountBuffer;

    //dynamic variable
    private int currentPointer;
    private boolean isMonitoring;
    private Exception monitoringException;
    private Thread monitorThread;

    //additional fields
    private long sleepInterval;
    private int bufferSize;
    private boolean initialized;
    private Runnable monitorRunnable;

    private static synchronized void registerMonitor() {
        CURRENTLY_RUN_MONITORS++;
    }


    private static synchronized void unregisterMonitor() {
        CURRENTLY_RUN_MONITORS--;
    }

    private static synchronized int countRunningMonitors() {
        return CURRENTLY_RUN_MONITORS;
    }

    public ResourcesUsageMonitor(long sleepInterval, int bufferSize) {
        this.sleepInterval = sleepInterval;
        this.bufferSize = bufferSize;
    }

    public void initialize() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        this.runtime = Runtime.getRuntime();

        //prepare getters
        Method freePhysicalMemorySizeGetters = operatingSystemMXBean.getClass().getMethod("getFreePhysicalMemorySize");
        Method processCpuLoadGetters = operatingSystemMXBean.getClass().getMethod("getProcessCpuLoad");
        Method systemCpuLoadGetters = operatingSystemMXBean.getClass().getMethod("getSystemCpuLoad");
        Method totalPhysicalMemorySizeGetters = operatingSystemMXBean.getClass().getMethod("getTotalPhysicalMemorySize");
        Method totalSwapSpaceSizeGetters = operatingSystemMXBean.getClass().getMethod("getTotalSwapSpaceSize");
        Method freeSwapSpaceSizeGetters = operatingSystemMXBean.getClass().getMethod("getFreeSwapSpaceSize");

        //make accessible
        freePhysicalMemorySizeGetters.setAccessible(true);
        processCpuLoadGetters.setAccessible(true);
        systemCpuLoadGetters.setAccessible(true);
        totalPhysicalMemorySizeGetters.setAccessible(true);
        totalSwapSpaceSizeGetters.setAccessible(true);
        freeSwapSpaceSizeGetters.setAccessible(true);

        //allocate memory for buffers
        this.applicationUseMemoryBuffer = new long[bufferSize];
        this.systemUseMemoryBuffer = new long[bufferSize];
        this.freeMemoryBuffer = new long[bufferSize];
        this.applicationCpuLoadBuffer = new double[bufferSize];
        this.systemCpuLoadBuffer = new double[bufferSize];
        this.concurrentlyMonitoringCountBuffer = new int[bufferSize];
        this.swapMemoryBuffer = new long[bufferSize];

        //runnable for monitoring
        monitorRunnable = () -> {
            currentPointer = 0;
            try {
                while (isMonitoring) {
                    //fill state to buffers
                    //kinds of cpu
                    applicationCpuLoadBuffer[currentPointer] = (double) processCpuLoadGetters.invoke(operatingSystemMXBean);
                    systemCpuLoadBuffer[currentPointer] = (double) systemCpuLoadGetters.invoke(operatingSystemMXBean);

                    //kinds of memory
                    freeMemoryBuffer[currentPointer] = (long) freePhysicalMemorySizeGetters.invoke(operatingSystemMXBean);
                    systemUseMemoryBuffer[currentPointer] = (long) totalPhysicalMemorySizeGetters.invoke(operatingSystemMXBean) // available memory
                            - freeMemoryBuffer[currentPointer] // free memory
                            - runtime.totalMemory(); // jvm used memory
                    applicationUseMemoryBuffer[currentPointer] = runtime.totalMemory();
                    swapMemoryBuffer[currentPointer] = (long) totalSwapSpaceSizeGetters.invoke(operatingSystemMXBean) - (long) freeSwapSpaceSizeGetters.invoke(operatingSystemMXBean);
                    concurrentlyMonitoringCountBuffer[currentPointer] = countRunningMonitors();

                    // increment pointer
                    currentPointer++;

                    Thread.sleep(sleepInterval);
                }
            } catch (Exception exception) {
                isMonitoring = false;
                monitoringException = exception;
            }

            unregisterMonitor();
        };

        this.initialized = true;
    }

    public void start() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        //check current state
        if (isMonitoring) {
            throw new IllegalStateException("Start monitoring before stop previous monitoring process");
        }
        if (!initialized) {
            throw new IllegalStateException("Resources monitor not initialized");
        }
        isMonitoring = true;
        registerMonitor();

        //start monitoring in new thread
        this.monitorThread = new Thread(monitorRunnable);
        this.monitorThread.setPriority(Thread.MAX_PRIORITY);
        this.monitorThread.start();
    }

    public void stop() {
        isMonitoring = false;
    }

    public List<State> getResult() throws Exception {
        if (monitorThread.isAlive()) {
            monitorThread.join();
        }
        //check state
        if (monitoringException != null) {
            throw monitoringException;
        }
        if (currentPointer == 0) {
            throw new IllegalArgumentException("No one recorded states");
        }

        List<State> states = new ArrayList<>(currentPointer);
        for (int i = 0; i < this.currentPointer; i++) {
            //create state POJO by buffers data
            State state = new State();
            state.setStateNumber(i);

            // fill state by buffers
            state.setApplicationUseMemory(applicationUseMemoryBuffer[i]);
            state.setSystemUseMemory(systemUseMemoryBuffer[i]);
            state.setFreeMemory(freeMemoryBuffer[i]);
            state.setSwapUseMemory(swapMemoryBuffer[i]);
            state.setApplicationCpuLoad(applicationCpuLoadBuffer[i]);
            state.setSystemCpuLoad(systemCpuLoadBuffer[i]);
            state.setConcurrentlyMonitoringCount(concurrentlyMonitoringCountBuffer[i]);

            states.add(state);
        }

        return states;
    }

    public Thread getMonitorThread() {
        return this.monitorThread;
    }
}
