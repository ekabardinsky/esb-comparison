package ru.ekabardinsky.magister.test.client.application.scheduler;

import com.google.gson.GsonBuilder;
import ru.ekabardinsky.magister.commons.schedule.model.Schedule;
import ru.ekabardinsky.magister.commons.schedule.model.ScheduleBoard;
import ru.ekabardinsky.magister.commons.utils.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ekabardinsky on 4/3/17.
 */
public class ResultSender {
    public void sendResult(ScheduleBoard scheduleBoard, Schedule schedule, Object results) throws IOException {
        HashMap<String, Object> result = new HashMap<>();
        result.put("schedule", schedule);
        result.put("results", results);

        String json = new GsonBuilder().create().toJson(result);
        HttpClient httpClient = new HttpClient();
        httpClient.post(scheduleBoard.getCallbackUrl(), json);
    }
}
