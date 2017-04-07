package ru.ekabardinsky.magister.test.client.application.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by ekabardinsky on 4/3/17.
 */
@RestController
public class HealthCheckController {
    @RequestMapping(method = GET, value = "/api/healthcheck")
    public HashMap<String, Object> get(@RequestBody String body) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        return response;
    }
}
