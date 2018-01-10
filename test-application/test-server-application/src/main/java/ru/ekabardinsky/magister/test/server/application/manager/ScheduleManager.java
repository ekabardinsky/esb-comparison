package ru.ekabardinsky.magister.test.server.application.manager;

import com.google.gson.internal.LinkedTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.LocaleEditor;
import ru.ekabardinsky.magister.commons.schedule.model.Schedule;
import ru.ekabardinsky.magister.commons.schedule.model.ScheduleBoard;
import ru.ekabardinsky.magister.commons.schedule.model.WorkerType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ekabardinsky on 4/4/17.
 */
public class ScheduleManager {
    @Value("${test.server.host}")
    private String host;

    @Value("${server.port}")
    private String port;

    @Value("${test.server.commitPath}")
    private String commitPath;

    @Autowired
    private EsbManager esbManager;

    public List<String> getAvailableTestCases() {
        HashMap esbDescription = esbManager.getEsbDescription();
        List testCases = (List) esbDescription.get("testCases");
        return (List<String>) testCases.stream()
                .map(x -> ((LinkedTreeMap) x).get("name"))
                .collect(Collectors.toList());
    }

    private Schedule generateSchedule(String name, Long delay, Long time, Long startIndex, Long count, Long sizeStep, Long repeatCount) {
        //create wrappers for time & delay
        Long delayWrapper = Long.valueOf(delay.toString());
        Long timeWrapper = Long.valueOf(time.toString());

        //get needed test case
        HashMap root = (HashMap) esbManager.getEsbDescription();
        List<LinkedTreeMap> testCases = (List<LinkedTreeMap>) root.get("testCases");
        LinkedTreeMap currentCase = testCases.stream()
                .filter(x -> name.equals(x.get("name")))
                .findFirst().get();

        //get test case params
        String workerType = (String) currentCase.get("workerType");
        WorkerType workerTypeWrapper = WorkerType.valueOf(workerType);
        LinkedTreeMap additionalParameters = (LinkedTreeMap) currentCase.get("additionalParameters");
        additionalParameters.put("startIndex", startIndex);
        additionalParameters.put("count", count);
        additionalParameters.put("sizeStep", sizeStep);
        additionalParameters.put("repeatCount", repeatCount);
        HashMap additionalParametersWrapper = new HashMap<>(additionalParameters);


        //assemble schedule
        Schedule schedule = new Schedule();
        schedule.setAdditionalParameters(additionalParametersWrapper);
        schedule.setWorkerType(workerTypeWrapper);
        schedule.setName(name);

        //date calculating
        Instant now = new Date().toInstant();
        Instant start = now.plusMillis(delayWrapper);
        Instant end = now.plusMillis(delayWrapper + timeWrapper);

        schedule.setStart(Date.from(start));
        schedule.setEndDate(Date.from(end));

        return schedule;
    }

    public ScheduleBoard generateScheduleBoard(List<HashMap> schedules) {
        ScheduleBoard scheduleBoard = new ScheduleBoard();
        scheduleBoard.setCallbackUrl("http://" + host + ":" + port + commitPath);

        List<Schedule> collect = schedules.stream().map(x -> {
            String name = (String) x.get("name");
            Long delay = Long.valueOf(x.get("delay").toString());
            Long time = Long.valueOf(x.get("time").toString());
            Long startIndex = Long.valueOf(x.get("startIndex").toString());
            Long count = Long.valueOf(x.get("count").toString());
            Long sizeStep = Long.valueOf(x.get("sizeStep").toString());
            Long repeatCount = Long.valueOf(x.get("repeatCount").toString());

            return generateSchedule(name, delay, time, startIndex, count, sizeStep, repeatCount);
        }).collect(Collectors.toList());

        scheduleBoard.setSchedules(collect);

        return scheduleBoard;
    }
}
