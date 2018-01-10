package ru.ekabardinsky.magister.camel.spring.boot.comparison.route;

import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spring.boot.FatJarRouter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ekabardinsky.magister.commons.soap.service.InboundService;

/**
 * Created by ekabardinsky on 4/11/17.
 */
@Component
public class APIRouteBuilder extends FatJarRouter {
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
                .get("/soap/outbound").to("direct:soap.outbound");

        from("cxf:soap/inbound?serviceClass=" + InboundService.class.getCanonicalName())
                .to("direct:soap.inbound");
    }
}
