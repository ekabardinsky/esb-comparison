package ru.ekabardinsky.magister.test.server.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ekabardinsky.magister.test.server.application.manager.ResultManager;
import ru.ekabardinsky.magister.test.server.application.model.ResultWrapper;

import javax.servlet.http.HttpServletResponse;
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

    @RequestMapping(method = GET, value = "/api/result/{case}.csv")
    public void getCsvResult(@PathVariable(value = "case") String testCase, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=utf-8");
        response.getWriter().append(resultManager.getStoreAsCsv(testCase));
    }
}
