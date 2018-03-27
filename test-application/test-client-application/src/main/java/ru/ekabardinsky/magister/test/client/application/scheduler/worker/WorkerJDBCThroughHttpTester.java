package ru.ekabardinsky.magister.test.client.application.scheduler.worker;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.execchain.MainClientExec;
import ru.ekabardinsky.magister.commons.Monitoring.ResourceManager;
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
public class WorkerJDBCThroughHttpTester extends Worker {

    public WorkerJDBCThroughHttpTester(Schedule schedule, ScheduleBoard scheduleBoard) {
        super(schedule, scheduleBoard);
    }

    @Override
    public void doTest(ResultSender resultSender) {
        List<HashMap<String, Object>> results = new ArrayList<>();

        Runnable runnable = () -> {
            try {
                Gson gson = new Gson();
                Long taskDuration = null;


                //prepare http client with additionalParameters
                HashMap<String, Object> additionalParameters = schedule.getAdditionalParameters();
                String url = (String) additionalParameters.get("url");

                // additional params
                int columnsCountStart = Double.valueOf(additionalParameters.get("columnsCountStart").toString()).intValue();
                int columnsCountEnd = Double.valueOf(additionalParameters.get("columnsCountEnd").toString()).intValue();
                int rowsCountStart = Double.valueOf(additionalParameters.get("rowsCountStart").toString()).intValue();
                int rowsCountEnd = Double.valueOf(additionalParameters.get("rowsCountEnd").toString()).intValue();
                List<String> serializeTypes = (List<String>) additionalParameters.get("serializeTypes");

                //calls to esb
                for(String serializeType : serializeTypes) {
                    for (int columnsCount = columnsCountStart; columnsCount <= columnsCountEnd; columnsCount++) {
                        for (int rowsCount = rowsCountStart; rowsCount <= rowsCountEnd; rowsCount++) {
                            System.out.println("------------Try to read data from db. Columns count " + columnsCount + ". Rows count " + rowsCount + ". Serialize to " + serializeType);

                            //configure request
                            HttpPost request = new HttpPost(url);
                            ResponseHandler<String> handler = new BasicResponseHandler();

                            //assemble body
                            HashMap<String, Object> body = new HashMap<>();
                            body.put("rowsCount", rowsCount);
                            body.put("columnsCount", columnsCount);
                            body.put("serializeType", serializeType);

                            //set body
                            HttpEntity entity = new StringEntity(gson.toJson(body));
                            request.setEntity(entity);

                            HttpClient client = HttpClientBuilder.create().build();

                            //call
                            long startTime = System.currentTimeMillis();
                            HttpResponse execute = client.execute(request);
                            long endTime = System.currentTimeMillis();
                            String responseBody = handler.handleResponse(execute);

                            //check response
                            if (execute.getStatusLine().getStatusCode() != 200) {
                                System.out.println("Error body:" + body);
                                throw new IllegalStateException("Wrong response");
                            }

                            //get results from header
                            String applicationUseMemory = execute.getFirstHeader("applicationUseMemory").getValue();
                            String systemUseMemory = execute.getFirstHeader("systemUseMemory").getValue();
                            String freeMemory = execute.getFirstHeader("freeMemory").getValue();
                            String swapUseMemory = execute.getFirstHeader("swapUseMemory").getValue();
                            String applicationCpuLoad = execute.getFirstHeader("applicationCpuLoad").getValue();
                            String systemCpuLoad = execute.getFirstHeader("systemCpuLoad").getValue();
                            String concurrentlyMonitoringCount = execute.getFirstHeader("concurrentlyMonitoringCount").getValue();

                            //record duration
                            taskDuration = endTime - startTime;

                            //assemble result
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("applicationUseMemory", applicationUseMemory);
                            result.put("systemUseMemory", systemUseMemory);
                            result.put("freeMemory", freeMemory);
                            result.put("swapUseMemory", swapUseMemory);
                            result.put("applicationCpuLoad", applicationCpuLoad);
                            result.put("systemCpuLoad", systemCpuLoad);
                            result.put("concurrentlyMonitoringCount", concurrentlyMonitoringCount);
                            result.put("duration", taskDuration);

                            //put input params too
                            result.put("serializeType", serializeType);
                            result.put("columnsCount", columnsCount);
                            result.put("rowsCount", rowsCount);

                            // put results
                            results.add(result);
                            System.out.println("Request completed");
                        }
                    }
                }


                resultSender.sendResult(scheduleBoard, schedule, results);
                System.out.println("Experiment done");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                setDone(true);
            }
        };

        new Thread(runnable).start();
    }
}
