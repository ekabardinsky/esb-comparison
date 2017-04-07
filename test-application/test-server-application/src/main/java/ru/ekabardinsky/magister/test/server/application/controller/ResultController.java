package ru.ekabardinsky.magister.test.server.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ekabardinsky.magister.test.server.application.manager.ResultManager;
import ru.ekabardinsky.magister.test.server.application.model.ResultWrapper;

import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by ekabardinsky on 4/7/17.
 */
@RestController
public class ResultController {
    @Autowired
    private ResultManager resultManager;

    @RequestMapping(method = GET, value = "/api/result")
    public ResultWrapper getResult() throws IOException {
        return resultManager.getStore();
    }
}
