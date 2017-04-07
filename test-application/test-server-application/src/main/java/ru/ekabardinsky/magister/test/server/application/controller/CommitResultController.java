package ru.ekabardinsky.magister.test.server.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ekabardinsky.magister.test.server.application.manager.ResultManager;

import java.util.HashMap;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by ekabardinsky on 4/6/17.
 */
@RestController
public class CommitResultController {
    @Autowired
    private ResultManager resultManager;

    @RequestMapping(method = POST, value = "/api/client/commit")
    public HashMap<String, Object> clientCommit(@RequestBody HashMap<String, Object> request) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        resultManager.commitClient(request);
        return response;
    }
}
