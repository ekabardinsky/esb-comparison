package ru.ekabardinsky.magister.test.server.application.controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ekabardinsky.magister.test.server.application.manager.ClientManager;
import ru.ekabardinsky.magister.test.server.application.manager.EsbManager;

import java.io.IOException;
import java.util.HashMap;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by ekabardinsky on 4/3/17.
 */
@RestController
public class RegistrationController {
    @Autowired
    public ClientManager clientManager;
    @Autowired
    public EsbManager esbManager;

    @RequestMapping(method = POST, value = "/api/client/register")
    public HashMap<String, Object> clientRegister(@RequestBody HashMap<String, Object> request) {
        return registerAction((host, port) -> clientManager.registerClient(host, port), request);
    }

    @RequestMapping(method = POST, value = "/api/esb/register")
    public HashMap<String, Object> esbRegister(@RequestBody HashMap<String, Object> request) {
        return registerAction((host, port) -> esbManager.register(host, port), request);
    }

    private HashMap<String, Object> registerAction(RegistrationAction register, HashMap<String, Object> request) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            //try to registration
            String host = (String) request.get("host");
            String port = (String) request.get("port");
            register.register(host, port);
            response.put("status", "OK");
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("errorMessage", e.getMessage());
        }
        return response;
    }

    interface RegistrationAction {
        void register(String host, String port) throws IOException;
    }
}
