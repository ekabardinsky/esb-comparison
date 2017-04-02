package ru.ekabardinsky.magister.commons.Monitoring;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekabardinsky on 3/27/17.
 */
public class ResourcesUsageMonitor {
    private OperatingSystemMXBean operatingSystemMXBean;
    private long sleepInterval;
    private int bufferSize;
    private boolean initialized;
    private Runnable monitorRunnable;

    //getters
    private Method freePhysicalMemorySizeGetters;
    private Method processCpuLoadGetters;

    //state buffers
    private Object[] freePhysicalMemoryBuffer;
    private Object[] systemCpuLoadBuffer;
    private long[] systemTimeBuffer;

    //dynamic variable
    private int currentPointer;
    private boolean isMonitoring;
    private Exception monitoringException;
    private Thread monitorThread;

    public ResourcesUsageMonitor(long sleepInterval, int bufferSize) {
        this.sleepInterval = sleepInterval;
        this.bufferSize = bufferSize;
    }

    public void initialize() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

        //prepare getters
        freePhysicalMemorySizeGetters = operatingSystemMXBean.getClass().getMethod("getFreePhysicalMemorySize");
        processCpuLoadGetters = operatingSystemMXBean.getClass().getMethod("getProcessCpuLoad");
        freePhysicalMemorySizeGetters.setAccessible(true);
        processCpuLoadGetters.setAccessible(true);

        //commit buffers
        this.freePhysicalMemoryBuffer = new Object[bufferSize];
        this.systemCpuLoadBuffer = new Object[bufferSize];
        this.systemTimeBuffer = new long[bufferSize];

        //runnable for monitoring
        monitorRunnable = () -> {
            currentPointer = 0;
            try {
                while (isMonitoring) {
                    //fill state to buffers
                    freePhysicalMemoryBuffer[currentPointer] = freePhysicalMemorySizeGetters.invoke(operatingSystemMXBean);
                    systemCpuLoadBuffer[currentPointer] = processCpuLoadGetters.invoke(operatingSystemMXBean);
                    systemTimeBuffer[currentPointer] = System.currentTimeMillis();
                    currentPointer++;

                    Thread.sleep(sleepInterval);
                }
            } catch (Exception exception) {
                isMonitoring = false;
                monitoringException = exception;
            }
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

        //get start state
        long startFreePhysicalMemory = (long) freePhysicalMemoryBuffer[0];
        long startSystemTime = systemTimeBuffer[0];

        List<State> states = new ArrayList<>(currentPointer);
        for (int i = 0; i < this.currentPointer; i++) {
            //create state POJO by buffers data
            State state = new State();
            state.setStateNumber(i);
            state.setSystemCpuLoad((double) systemCpuLoadBuffer[i]);
            state.setUsedMemory(startFreePhysicalMemory - (long) freePhysicalMemoryBuffer[i]);
            state.setSystemTime(systemTimeBuffer[i] - startSystemTime);

            states.add(state);
        }

        return states;
    }

    public Thread getMonitorThread() {
        return this.monitorThread;
    }
}
