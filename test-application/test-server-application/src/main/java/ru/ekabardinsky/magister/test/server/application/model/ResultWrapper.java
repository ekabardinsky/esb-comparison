package ru.ekabardinsky.magister.test.server.application.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ekabardinsky on 4/6/17.
 */
public class ResultWrapper {
    private HashMap<String, Object> esbResults;
    private HashMap<String, Object> clientResults;

    public ResultWrapper() {
        clientResults = new HashMap<>();
        esbResults = new HashMap<>();
    }

    public HashMap<String, Object> getEsbResults() {
        return esbResults;
    }

    public void setEsbResults(HashMap<String, Object> esbResults) {
        this.esbResults = esbResults;
    }

    public HashMap<String, Object> getClientResults() {
        return clientResults;
    }

    public void setClientResults(HashMap<String, Object> clientResults) {
        this.clientResults = clientResults;
    }
}
