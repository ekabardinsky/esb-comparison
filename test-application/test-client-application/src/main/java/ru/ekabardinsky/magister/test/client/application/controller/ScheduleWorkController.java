package ru.ekabardinsky.magister.test.client.application.controller;

import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ekabardinsky.magister.commons.schedule.model.Schedule;
import ru.ekabardinsky.magister.commons.schedule.model.ScheduleBoard;
import ru.ekabardinsky.magister.test.client.application.scheduler.TestCaseRunner;

import java.util.HashMap;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by ekabardinsky on 4/3/17.
 */
@RestController
public class ScheduleWorkController {
    @Autowired
    private TestCaseRunner testCaseRunner;

    @RequestMapping(method = POST, value = "/api/schedule")
    public HashMap<String, Object> post(@RequestBody String body) {
        ScheduleBoard scheduleBoard = new GsonBuilder().create().fromJson(body, ScheduleBoard.class);
        return testCaseRunner.runTestCase(scheduleBoard);
    }
}
