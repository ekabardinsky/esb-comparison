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

    private Schedule generateSchedule(String name,
                                      Long delay,
                                      Long columnsCountStart,
                                      Long columnsCountEnd,
                                      Long rowsCountStart,
                                      Long rowsCountEnd,
                                      List<String> serializeTypes,
                                      Long clientsCountStart,
                                      Long clientsCountEnd) {
        //get needed test case
        HashMap root = esbManager.getEsbDescription();
        List<LinkedTreeMap> testCases = (List<LinkedTreeMap>) root.get("testCases");
        LinkedTreeMap currentCase = testCases.stream()
                .filter(x -> name.equals(x.get("name")))
                .findFirst().get();

        //get test case params
        String workerType = (String) currentCase.get("workerType");
        WorkerType workerTypeWrapper = WorkerType.valueOf(workerType);
        LinkedTreeMap additionalParameters = (LinkedTreeMap) currentCase.get("additionalParameters");

        // map additional params
        additionalParameters.put("columnsCountStart", columnsCountStart);
        additionalParameters.put("columnsCountEnd", columnsCountEnd);
        additionalParameters.put("rowsCountStart", rowsCountStart);
        additionalParameters.put("rowsCountEnd", rowsCountEnd);
        additionalParameters.put("serializeTypes", serializeTypes);
        additionalParameters.put("clientsCountStart", clientsCountStart);
        additionalParameters.put("clientsCountEnd", clientsCountEnd);

        HashMap additionalParametersWrapper = new HashMap<>(additionalParameters);


        //assemble schedule
        Schedule schedule = new Schedule();
        schedule.setAdditionalParameters(additionalParametersWrapper);
        schedule.setWorkerType(workerTypeWrapper);
        schedule.setName(name);
        schedule.setDelay(delay);

        return schedule;
    }

    public ScheduleBoard generateScheduleBoard(List<HashMap> schedules) {
        ScheduleBoard scheduleBoard = new ScheduleBoard();
        scheduleBoard.setCallbackUrl("http://" + host + ":" + port + commitPath);

        List<Schedule> collect = schedules.stream().map(x -> {
            String name = (String) x.get("name");
            Long delay = Long.valueOf(x.get("delay").toString());
            Long columnsCountStart = Long.valueOf(x.get("columnsCountStart").toString());
            Long columnsCountEnd = Long.valueOf(x.get("columnsCountEnd").toString());
            Long rowsCountStart = Long.valueOf(x.get("rowsCountStart").toString());
            Long rowsCountEnd = Long.valueOf(x.get("rowsCountEnd").toString());
            Long clientsCountStart = Long.valueOf(x.get("clientsCountStart").toString());
            Long clientsCountEnd = Long.valueOf(x.get("clientsCountEnd").toString());
            List<String> serializeTypes = (List<String>) x.get("serializeType");


            return generateSchedule(name, delay, columnsCountStart, columnsCountEnd, rowsCountStart, rowsCountEnd, serializeTypes, clientsCountStart, clientsCountEnd);
        }).collect(Collectors.toList());

        scheduleBoard.setSchedules(collect);

        return scheduleBoard;
    }
}
