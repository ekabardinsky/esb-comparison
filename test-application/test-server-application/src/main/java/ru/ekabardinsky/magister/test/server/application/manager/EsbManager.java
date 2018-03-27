package ru.ekabardinsky.magister.test.server.application.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ekabardinsky.magister.commons.schedule.model.Schedule;
import ru.ekabardinsky.magister.commons.schedule.model.ScheduleBoard;
import ru.ekabardinsky.magister.commons.utils.HttpClient;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ekabardinsky on 4/4/17.
 */
public class EsbManager {
    @Autowired
    private ResultManager resultManager;
    @Autowired
    private HttpClient httpClient;

    private String host;
    private String port;
    private HashMap esbDescription;

    private boolean hasStarted;

    public void register(String host, String port) throws IOException {
        String url = "http://" + host + ":" + port + "/api/testcases";
        String response = httpClient.get(url);
        HashMap hashMap = new GsonBuilder().create().fromJson(response, HashMap.class);
        this.esbDescription = hashMap;
        this.host = host;
        this.port = port;
    }

    public void start(ScheduleBoard scheduleBoard) throws IOException {
        if (hasStarted) {
            throw new IllegalStateException("Monitoring already started");
        }
        hasStarted = true;
        String url = "http://" + host + ":" + port + "/api/monitor/start";

        //every schedule run in their own thread
        scheduleBoard.getSchedules().forEach(x -> {
            Runnable monitoringTask = () -> {
                try {
                    //sleep upon start date
                    Thread.sleep(x.getDelay());

                    //main action, just start esb monitoring
                    httpClient.get(url);
                } catch (Exception e) {
                    e.printStackTrace();
                    hasStarted = false;
                }
            };
            //lets go monitoring!
            new Thread(monitoringTask).start();
        });


    }

    public void stop(Schedule schedule) throws IOException {
        String url = "http://" + host + ":" + port + "/api/monitor/stop";
        String response = httpClient.get(url);
        Gson gson = new GsonBuilder().create();
        List result = gson.fromJson(response, List.class);

        //little trick for convert pojo to Map
        String tempSchedule = gson.toJson(schedule);
        HashMap scheduleWrapper = gson.fromJson(tempSchedule, HashMap.class);

        resultManager.commitEsb(scheduleWrapper, result);
        hasStarted = false;
    }

    public HashMap getEsbDescription() {
        return esbDescription;
    }

    public boolean isStarted() {
        return hasStarted;
    }
}
