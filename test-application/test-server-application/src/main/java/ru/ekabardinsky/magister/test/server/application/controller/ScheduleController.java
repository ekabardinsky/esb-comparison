package ru.ekabardinsky.magister.test.server.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ekabardinsky.magister.commons.schedule.model.ScheduleBoard;
import ru.ekabardinsky.magister.test.server.application.manager.ClientManager;
import ru.ekabardinsky.magister.test.server.application.manager.EsbManager;
import ru.ekabardinsky.magister.test.server.application.manager.ScheduleManager;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by ekabardinsky on 4/4/17.
 */
@RestController
public class ScheduleController {
    @Autowired
    private ScheduleManager scheduleManager;

    @Autowired
    private ClientManager clientManager;

    @Autowired
    private EsbManager esbManager;

    @RequestMapping(method = GET, value = "/api/esb/testcases")
    public List<String> availableCases() {
        return scheduleManager.getAvailableTestCases();
    }

    @RequestMapping(method = POST, value = "/api/esb/testcases/run")
    public HashMap<String, Object> runCases(@RequestBody List<HashMap> schedules) throws IOException {
        HashMap<String, Object> response = new HashMap<>();
        if (checkTestAvailable(schedules)) {
            ScheduleBoard scheduleBoard = scheduleManager.generateScheduleBoard(schedules);
            clientManager.sendScheduleBoard(scheduleBoard);
           // esbManager.start(scheduleBoard);
            response.put("status", "OK");
        } else {
            response.put("status", "ERROR");
            response.put("info", "TestCases can't be started. Do you register any esb and client? Maybe another test was run? Please check also availability for cases.");
        }
        return response;
    }

    private boolean checkTestAvailable(List<HashMap> schedules) {
        HashMap esbDescription = esbManager.getEsbDescription();
        //do you register any client and esb?
        if (esbDescription != null
                && esbDescription.containsKey("testCases")
                && clientManager.getClients().size() > 0
                && !esbManager.isStarted()) {

            //do you pass valid names?
            List<AbstractMap> testCases = (List) esbDescription.get("testCases");
            boolean availableTestCases = schedules.stream()
                    .allMatch(passed -> {
                        return testCases.stream()
                                .anyMatch(available -> available.get("name").equals(passed.get("name")));
                    });

            return availableTestCases;
        } else {
            //not today
            return false;
        }
    }
}
