package ru.ekabardinsky.magister.commons.Monitoring;

/**
 * Created by ekabardinsky on 3/27/17.
 */
public class State {
    private long usedMemory;
    private double systemCpuLoad;
    private long systemTime;
    private int stateNumber;

    public long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public double getSystemCpuLoad() {
        return systemCpuLoad;
    }

    public void setSystemCpuLoad(double systemCpuLoad) {
        this.systemCpuLoad = systemCpuLoad;
    }

    public long getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(long systemTime) {
        this.systemTime = systemTime;
    }

    public int getStateNumber() {
        return stateNumber;
    }

    public void setStateNumber(int stateNumber) {
        this.stateNumber = stateNumber;
    }

    public String toString() {
        return "№ " + stateNumber +
                " UsedMemory: " + usedMemory +
                " SystemCpu: " + systemCpuLoad +
                " SystemTime: " + systemTime;
    }
}
