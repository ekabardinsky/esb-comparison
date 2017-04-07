package ru.ekabardinsky.magister.commons.schedule.model;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ekabardinsky on 4/3/17.
 */
public class Schedule {
    private UUID id;
    private Date start;
    private WorkerType workerType;
    private Date endDate;
    private String name;
    private HashMap<String, Object> additionalParameters;

    public Schedule() {
        id = UUID.randomUUID();
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public WorkerType getWorkerType() {
        return workerType;
    }

    public void setWorkerType(WorkerType workerType) {
        this.workerType = workerType;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Object> getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(HashMap<String, Object> additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}