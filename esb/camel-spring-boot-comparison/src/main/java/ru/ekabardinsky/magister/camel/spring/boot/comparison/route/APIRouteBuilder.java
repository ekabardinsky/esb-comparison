package ru.ekabardinsky.magister.camel.spring.boot.comparison.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by ekabardinsky on 4/11/17.
 */
@Component
public class APIRouteBuilder extends RouteBuilder {
    @Value("${esb.port}")
    private Integer esbPort;

    @Override
    public void configure() {
        restConfiguration()
                .component("restlet")
                .host("localhost")
                .port(esbPort);

        rest("/api")
                .get("/testcases").to("direct:testcases")
                .post("/ftp/outbound").to("direct:ftp.outbound")
                .get("/monitor/start").to("direct:monitor.start")
                .get("/monitor/stop").to("direct:monitor.stop")
                .get("/rest/outbound").to("direct:rest.outbound")
                .get("/rest/inbound").to("direct:rest.inbound")
                .post("/jdbc/read").to("direct:jdbc.read");
    }
}
