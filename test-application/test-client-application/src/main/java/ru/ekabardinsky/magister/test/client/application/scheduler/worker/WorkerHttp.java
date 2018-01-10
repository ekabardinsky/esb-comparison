package ru.ekabardinsky.magister.test.client.application.scheduler.worker;

import com.google.gson.internal.LinkedTreeMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.execchain.MainClientExec;
import ru.ekabardinsky.magister.commons.schedule.model.Schedule;
import ru.ekabardinsky.magister.commons.schedule.model.ScheduleBoard;
import ru.ekabardinsky.magister.test.client.application.scheduler.ResultSender;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by ekabardinsky on 4/3/17.
 */
public class WorkerHttp extends Worker {

    public WorkerHttp(Schedule schedule, ScheduleBoard scheduleBoard) {
        super(schedule, scheduleBoard);
    }

    @Override
    public void doTest(ResultSender resultSender) {
        LocalDateTime start = LocalDateTime.ofInstant(schedule.getStart().toInstant(), ZoneId.systemDefault());
        List<HashMap<String, Object>> results = new ArrayList<>();

        Runnable runnable = () -> {
            try {
                List<Long> durations = new ArrayList<>();

                //prepare http client with additionalParameters
                HashMap<String, Object> additionalParameters = schedule.getAdditionalParameters();
                String url = (String) additionalParameters.get("url");

                //configure request
                HttpRequestBase request = null;
                if (additionalParameters.containsKey("method")) {
                    String method = (String) additionalParameters.get("method");

                    if ("POST".equals(method)) {
                        request = new HttpPost(url);
                    } else if ("GET".equals(method)) {
                        request = new HttpGet(url);
                    } else {
                        throw new IllegalArgumentException("Not supported http method");
                    }
                } else {
                    request = new HttpGet(url);
                }

                //set headers
                if (additionalParameters.containsKey("headers")) {
                    LinkedTreeMap headers = (LinkedTreeMap) additionalParameters.get("headers");
                    Set set = headers.keySet();
                    HttpRequestBase finalRequest = request;
                    set.forEach(x -> {
                        finalRequest.addHeader((String) x, headers.get(x).toString());
                    });
                }
                //set body
                if (additionalParameters.containsKey("body")) {
                    String body = (String) additionalParameters.get("body");
                    HttpEntity entity = new ByteArrayEntity(body.getBytes("UTF-8"));
                    ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
                }

                //sleep upon start date
                LocalDateTime localNow = LocalDateTime.now();
                Duration duration = Duration.between(localNow, start);
                long milliseconds = duration.toMillis();
                Thread.sleep(milliseconds);

                //calls to esb
                //prepare call
                HttpClient client = HttpClientBuilder.create().build();

                //call
                long startTime = System.currentTimeMillis();
                HttpResponse execute = client.execute(request);
                long endTime = System.currentTimeMillis();

                //check response
                if (execute.getStatusLine().getStatusCode() != 200) {
                    throw new IllegalStateException("Wrong response");
                }

                //record duration
                durations.add(endTime - startTime);

                resultSender.sendResult(scheduleBoard, schedule, durations);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                setDone(true);
            }
        };

        new Thread(runnable).start();
    }
}
