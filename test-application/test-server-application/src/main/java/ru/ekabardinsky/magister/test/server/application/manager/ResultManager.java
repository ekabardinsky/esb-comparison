package ru.ekabardinsky.magister.test.server.application.manager;

import com.google.gson.GsonBuilder;
import ru.ekabardinsky.magister.commons.schedule.model.Schedule;
import ru.ekabardinsky.magister.test.server.application.model.ResultWrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Created by ekabardinsky on 4/4/17.
 */
public class ResultManager {
    private ResultWrapper store;

    public ResultManager() {
        store = new ResultWrapper();
    }

    public void commitClient(HashMap<String, Object> result) {
        HashMap clients = store.getClientResults();
        AbstractMap schedule = (AbstractMap) result.get("schedule");
        Object name = schedule.get("name");

        commit(clients, result, name);
    }

    public void commitEsb(HashMap schedule, List result) {
        HashMap<String, Object> resultObject = new HashMap<>();
        resultObject.put("results", result);
        resultObject.put("schedule", schedule);

        HashMap esbResults = store.getEsbResults();
        Object name = schedule.get("name");

        commit(esbResults, resultObject, name);
    }

    private void commit(HashMap results, HashMap resultObject, Object name) {
        if (results.containsKey(name)) {
            List<HashMap> resultWrapper = (List<HashMap>) results.get(name);
            resultWrapper.add(resultObject);
        } else {
            List<HashMap> resultWrapper = new ArrayList<>();
            resultWrapper.add(resultObject);
            results.put(name, resultWrapper);
        }
    }

    public void save() {
        String result = new GsonBuilder().create().toJson(store);
        String fileName = new Date().getTime() + "-result.json";
        try {
            Files.write(Paths.get("./" + fileName), result.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ResultWrapper getStore() {
        return store;
    }

    public StringBuffer getStoreAsCsv(String testCase) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        List<Map<String, Object>> completedTests = (List<Map<String, Object>>) getStore()
                .getClientResults()
                .get(testCase);

        completedTests.forEach(test -> {
            if (test.containsKey("results")) {
                resultList.addAll((Collection<? extends Map<String, Object>>) test.get("results"));
            }
        });

        return toCSV(resultList);
    }

    private static StringBuffer toCSV(List<Map<String, Object>> list) {
        List<String> headers = list.stream().flatMap(map -> map.keySet().stream()).distinct().collect(toList());
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < headers.size(); i++) {
            sb.append(headers.get(i));
            sb.append(i == headers.size() - 1 ? "\n" : ";");
        }
        for (Map<String, Object> map : list) {
            for (int i = 0; i < headers.size(); i++) {
                sb.append(map.get(headers.get(i)));
                sb.append(i == headers.size() - 1 ? "\n" : ";");
            }
        }
        return sb;
    }
}
