package ru.ekabardinsky.magister.commons.schedule.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekabardinsky on 4/3/17.
 */
public class ScheduleBoard {
    private String callbackUrl;
    private List<Schedule> schedules = new ArrayList<>();

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}
