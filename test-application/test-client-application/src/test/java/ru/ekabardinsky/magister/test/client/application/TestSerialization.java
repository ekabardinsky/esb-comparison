package ru.ekabardinsky.magister.test.client.application;

import com.google.gson.GsonBuilder;
import ru.ekabardinsky.magister.commons.schedule.model.Schedule;
import ru.ekabardinsky.magister.commons.schedule.model.ScheduleBoard;
import ru.ekabardinsky.magister.commons.schedule.model.WorkerType;

import java.util.Date;
import java.util.List;

/**
 * Created by ekabardinsky on 4/3/17.
 */
public class TestSerialization {
    public static void main(String [] args) {
        Schedule schedule = new Schedule();
        schedule.setEndDate(new Date());
        schedule.setStart(new Date());
        schedule.setWorkerType(WorkerType.Http);
        ScheduleBoard scheduleBoard = new ScheduleBoard();
        scheduleBoard.getSchedules().add(schedule);
        String temp = new GsonBuilder().create().toJson(scheduleBoard);

        System.out.println(temp);
        ScheduleBoard scheduleBoard1 = new GsonBuilder().create()
                .fromJson(temp, ScheduleBoard.class);
        System.out.println(scheduleBoard1.getSchedules().size());
    }
}
