package ru.ekabardinsky.magister.test.client.application.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.ekabardinsky.magister.commons.schedule.model.Schedule;
import ru.ekabardinsky.magister.commons.schedule.model.ScheduleBoard;
import ru.ekabardinsky.magister.test.client.application.scheduler.worker.Worker;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ekabardinsky on 4/3/17.
 */
public class TestCaseRunner {
    @Autowired
    private ResultSender resultSender;

    private List<Worker> workers;

    public HashMap<String, Object> runTestCase(ScheduleBoard scheduleBoard) {
        HashMap<String, Object> response = new HashMap<>();


        if (workers == null || workers.stream().allMatch(Worker::isDone)) {
            createWorkers(scheduleBoard);
            response.put("status", "OK");
            return response;
        } else {
            response.put("status", "ERROR");
            response.put("info", "Awaiting some worker for done");
            return response;
        }
    }

    private void createWorkers(ScheduleBoard scheduleBoard) {
        this.workers = scheduleBoard
                .getSchedules().stream()
                .map((schedule) -> Worker.getInstance(schedule, scheduleBoard))
                .collect(Collectors.toList());
        this.workers.forEach(x -> x.doTest(resultSender));
    }

    public ResultSender getResultSender() {
        return resultSender;
    }

    public void setResultSender(ResultSender resultSender) {
        this.resultSender = resultSender;
    }
}
