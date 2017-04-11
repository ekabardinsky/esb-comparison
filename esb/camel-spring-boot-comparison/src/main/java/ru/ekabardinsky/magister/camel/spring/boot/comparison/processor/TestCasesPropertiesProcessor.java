package ru.ekabardinsky.magister.camel.spring.boot.comparison.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;

/**
 * Created by ekabardinsky on 4/11/17.
 */
public class TestCasesPropertiesProcessor implements Processor {
    @Value("${esb.host}")
    private String host;

    @Value("${esb.port}")
    private String port;

    @Value("${esb.basePath}")
    private String basePath;


    public void process(Exchange exchange) throws Exception {
        HashMap<String, String> placeholder = new HashMap<String, String>();
        placeholder.put("host", host);
        placeholder.put("port", port);
        placeholder.put("basePath", basePath);
        exchange.getOut().setBody(placeholder);
    }
}
