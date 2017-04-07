package ru.ekabardinsky.magister.test.client.application.scheduler.worker;

import ru.ekabardinsky.magister.commons.schedule.model.Schedule;
import ru.ekabardinsky.magister.commons.schedule.model.ScheduleBoard;
import ru.ekabardinsky.magister.commons.schedule.model.WorkerType;
import ru.ekabardinsky.magister.test.client.application.scheduler.ResultSender;

/**
 * Created by ekabardinsky on 4/3/17.
 */
public abstract class Worker {
    private boolean isDone;
    protected Schedule schedule;
    protected ScheduleBoard scheduleBoard;

    Worker(Schedule schedule, ScheduleBoard scheduleBoard) {
        this.schedule = schedule;
        this.scheduleBoard = scheduleBoard;
    }

    //return: how long was every call?
    public abstract void doTest(ResultSender resultSender);

    //factory
    public static Worker getInstance(Schedule schedule, ScheduleBoard scheduleBoard) {
        if (WorkerType.Http.equals(schedule.getWorkerType())) {
            return new WorkerHttp(schedule, scheduleBoard);
        }
        throw new IllegalArgumentException("Have not worker for test case " + schedule.getWorkerType());
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
