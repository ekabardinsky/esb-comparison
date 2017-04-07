package ru.ekabardinsky.magister.test.server.application.manager;

import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ekabardinsky.magister.commons.schedule.model.ScheduleBoard;
import ru.ekabardinsky.magister.commons.utils.HttpClient;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ekabardinsky on 4/4/17.
 */
public class ClientManager {
    @Autowired
    private HttpClient httpClient;

    private Set<AbstractMap.SimpleEntry> clients;

    public ClientManager() {
        clients = new HashSet<>();
    }

    public void registerClient(String host, String port) throws IOException {
        //construct url
        String url = "http://" + host + ":" + port + "/api/healthcheck";

        httpClient.get(url);
        clients.add(new AbstractMap.SimpleEntry(host, port));
    }

    public void sendScheduleBoard(ScheduleBoard board) {
        String json = new GsonBuilder().create().toJson(board);

        //send schedule to all clients
        clients.forEach(client -> {
            try {
                String url = "http://" + client.getKey() + ":" + client.getValue() + "/api/schedule";
                httpClient.post(url, json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Set<AbstractMap.SimpleEntry> getClients() {
        return clients;
    }
}
