package ru.ekabardinsky.magister.commons.Monitoring;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by ekabardinsky on 3/27/17.
 */
public class State {
    private long applicationUseMemory;
    private long systemUseMemory;
    private long freeMemory;
    private long swapUseMemory;
    private double applicationCpuLoad;
    private double systemCpuLoad;
    private int stateNumber;
    private int concurrentlyMonitoringCount;


    // getters && setters
    public long getSwapUseMemory() {
        return swapUseMemory;
    }

    public void setSwapUseMemory(long swapUseMemory) {
        this.swapUseMemory = swapUseMemory;
    }

    public int getConcurrentlyMonitoringCount() {
        return concurrentlyMonitoringCount;
    }

    public void setConcurrentlyMonitoringCount(int concurrentlyMonitoringCount) {
        this.concurrentlyMonitoringCount = concurrentlyMonitoringCount;
    }

    public long getApplicationUseMemory() {
        return applicationUseMemory;
    }

    public void setApplicationUseMemory(long applicationUseMemory) {
        this.applicationUseMemory = applicationUseMemory;
    }

    public long getSystemUseMemory() {
        return systemUseMemory;
    }

    public void setSystemUseMemory(long systemUseMemory) {
        this.systemUseMemory = systemUseMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public double getApplicationCpuLoad() {
        return applicationCpuLoad;
    }

    public void setApplicationCpuLoad(double applicationCpuLoad) {
        this.applicationCpuLoad = applicationCpuLoad;
    }

    public double getSystemCpuLoad() {
        return systemCpuLoad;
    }

    public void setSystemCpuLoad(double systemCpuLoad) {
        this.systemCpuLoad = systemCpuLoad;
    }

    public int getStateNumber() {
        return stateNumber;
    }

    public void setStateNumber(int stateNumber) {
        this.stateNumber = stateNumber;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
