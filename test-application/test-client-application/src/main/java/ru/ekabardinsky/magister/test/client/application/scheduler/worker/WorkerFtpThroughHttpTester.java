package ru.ekabardinsky.magister.test.client.application.scheduler.worker;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import ru.ekabardinsky.magister.commons.Monitoring.ResourceManager;
import ru.ekabardinsky.magister.commons.schedule.model.Schedule;
import ru.ekabardinsky.magister.commons.schedule.model.ScheduleBoard;
import ru.ekabardinsky.magister.test.client.application.scheduler.ResultSender;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by ekabardinsky on 4/3/17.
 */
public class WorkerFtpThroughHttpTester extends Worker {

    private static final int COUNT = 5;
    private static final int SIZE = 1000;
    private static final int REPEAT_COUNT = 1;

    public WorkerFtpThroughHttpTester(Schedule schedule, ScheduleBoard scheduleBoard) {
        super(schedule, scheduleBoard);
    }

    @Override
    public void doTest(ResultSender resultSender) {
        LocalDateTime start = LocalDateTime.ofInstant(schedule.getStart().toInstant(), ZoneId.systemDefault());
        List<HashMap<String, Object>> results = new ArrayList<>();

        Runnable runnable = () -> {
            try {
                Long taskDuration = null;


                //prepare http client with additionalParameters
                HashMap<String, Object> additionalParameters = schedule.getAdditionalParameters();
                String url = (String) additionalParameters.get("url");


                //sleep upon start date
                LocalDateTime localNow = LocalDateTime.now();
                Duration duration = Duration.between(localNow, start);
                long milliseconds = duration.toMillis();
                Thread.sleep(milliseconds);

                //calls to esb
                for (int i = 0; i < COUNT; i++) {
                    for (int attempt = 0; attempt < REPEAT_COUNT; attempt++) {
                        int generateSize = (i + 1) * SIZE;

                        //configure request
                        HttpRequestBase request = null;
                        ResponseHandler<String> handler = new BasicResponseHandler();
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
                        HttpEntity entity = new ByteArrayEntity(ResourceManager.getTestResource(generateSize));
                        ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
                        //prepare call
                        HttpClient client = HttpClientBuilder.create().build();

                        //call
                        long startTime = System.currentTimeMillis();
                        HttpResponse execute = client.execute(request);
                        long endTime = System.currentTimeMillis();
                        String body = handler.handleResponse(execute);

                        //check response
                        if (execute.getStatusLine().getStatusCode() != 200) {
                            System.out.println("Error body:" + body);
                            throw new IllegalStateException("Wrong response");
                        }

                        //record duration
                        ArrayList responseFromEsb = new Gson().fromJson(body, ArrayList.class);
                        taskDuration = endTime - startTime;

                        HashMap<String, Object> wrapper = new HashMap<>();
                        wrapper.put("duration", taskDuration);
                        wrapper.put("esbResponse", responseFromEsb);
                        wrapper.put("size", generateSize);

                        results.add(wrapper);
                    }
                }

                resultSender.sendResult(scheduleBoard, schedule, results);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                setDone(true);
            }
        };

        new Thread(runnable).start();
    }
}
