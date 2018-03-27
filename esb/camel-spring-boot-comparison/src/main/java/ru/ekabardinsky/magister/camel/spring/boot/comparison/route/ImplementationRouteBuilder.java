package ru.ekabardinsky.magister.camel.spring.boot.comparison.route;

import com.google.gson.Gson;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tempuri.purchaseorderschema.Items;
import org.tempuri.purchaseorderschema.PurchaseOrderType;
import org.tempuri.purchaseorderschema.USAddress;
import ru.ekabardinsky.magister.camel.spring.boot.comparison.processor.AssembleResponseProcessor;
import ru.ekabardinsky.magister.camel.spring.boot.comparison.processor.JdbcAssembleQueryProcessor;
import ru.ekabardinsky.magister.camel.spring.boot.comparison.processor.MonitorCreationProcessor;
import ru.ekabardinsky.magister.camel.spring.boot.comparison.processor.TestCasesPropertiesProcessor;
import ru.ekabardinsky.magister.commons.Monitoring.ResourcesUsageMonitor;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by ekabardinsky on 4/11/17.
 */
@Component
public class ImplementationRouteBuilder extends RouteBuilder {
    private PurchaseOrderType examplePurchaseOrder;

    @Value("${rest.host}")
    private String restHost;

    @Value("${rest.port}")
    private String restPort;

    @Value("${rest.basePath}")
    private String restBasePath;

    @Value("${ftp.host}")
    private String ftpHost;

    @Value("${ftp.port}")
    private String ftpPort;

    @Value("${ftp.path}")
    private String ftpPath;

    @Value("${ftp.username}")
    private String ftpUsername;

    @Value("${ftp.password}")
    private String ftpPassword;

    @Autowired
    private TestCasesPropertiesProcessor testCasesPropertiesProcessor;

    @Autowired
    private JdbcAssembleQueryProcessor jdbcAssembleQueryProcessor;

    @Autowired
    private MonitorCreationProcessor monitorCreationProcessor;

    @Autowired
    private AssembleResponseProcessor assembleResponseProcessor;

    @Autowired
    private ResourcesUsageMonitor resourcesUsageMonitor;

    @Autowired
    private Gson gson;

    public ImplementationRouteBuilder() {
        this.examplePurchaseOrder = getExample();
    }

    @Override
    public void configure() {
        from("direct:testcases")
                .process(testCasesPropertiesProcessor)
                .setBody().groovy("resource:classpath:getTestCases.groovy")
                .process(e -> e.getOut().setBody(gson.toJson(e.getIn().getBody())));

        from("direct:monitor.start")
                .process(x -> System.out.println("resourcesUsageMonitor.start()"));

        from("direct:monitor.stop")
                .process(x -> System.out.println("resourcesUsageMonitor.stop()"));
//                .process(x -> x.getOut().setBody(resourcesUsageMonitor.getResult()));

        from("direct:rest.inbound")
                .setBody(constant(null));

        from("direct:rest.outbound")
                .removeHeaders("*")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .to("http://" + restHost +
                        ":" + restPort +
                        restBasePath + "/rest/post")
                .unmarshal().json(JsonLibrary.Gson);

        from("direct:ftp.outbound")
                .process(e -> System.out.println("started"))
                .setExchangePattern(ExchangePattern.InOut)
                .process(e -> resourcesUsageMonitor.start())
                .removeHeaders("*")
                .process((exchange1 -> System.out.println(exchange1.getIn().getBody())))
                .process((exchange) -> {
                    exchange.getOut().setBody(exchange.getIn().getBody());
                    exchange.getOut().setHeader("CamelFileName", UUID.randomUUID().toString());
                })
                .process(e -> System.out.println("File prepared"))
                .to("ftp://" + ftpUsername +
                        "@" + ftpHost +
                        ":" + ftpPort + ftpPath +
                        "?password=" + ftpPassword +
                        "&binary=true")

                .process(e -> System.out.println("File sent"))
                .process(e -> {
                    resourcesUsageMonitor.stop();
                    e.getOut().setBody(gson.toJson(resourcesUsageMonitor.getResult()));
                    e.getOut().getHeader("Content-Type", "application/json");
                })
                .process(e -> System.out.println("Stoped"));

        from("direct:jdbc.read")
                .setExchangePattern(ExchangePattern.InOut)
                .process(jdbcAssembleQueryProcessor)
                .process(monitorCreationProcessor)
                .to("jdbc:dataSource")
                .choice()
                    .when(property("serializeType").isEqualTo("JSON")).to("direct:toJson")
                    .when(property("serializeType").isEqualTo("XML")).to("direct:toXml")
                    .when(property("serializeType").isEqualTo("CSV")).to("direct:toCsv");

        from("direct:toJson")
                .setExchangePattern(ExchangePattern.InOut)
                .marshal().json(JsonLibrary.Gson)
                .to("direct:assembleResult");
        from("direct:toXml")
                .marshal().jacksonxml()
                .to("direct:assembleResult");
        from("direct:toCsv")
                .marshal().csv()
                .to("direct:assembleResult");

        from("direct:assembleResult")
                .process(assembleResponseProcessor);

    }

    private PurchaseOrderType getExample() {
        PurchaseOrderType purchaseOrder = new PurchaseOrderType();

        purchaseOrder.setOrderDate(getXmlGregorianCalendar());
        purchaseOrder.setConfirmDate(getXmlGregorianCalendar());

        //ship to
        USAddress shipTo = new USAddress();
        shipTo.setName("Purchase order");
        shipTo.setCountry("RU");
        shipTo.setZip(new BigDecimal("625013"));
        shipTo.setStreet("50 let oktyabrya");
        shipTo.setCity("Tyumen");
        shipTo.setState("Tyumen");
        purchaseOrder.setShipTo(shipTo);

        purchaseOrder.setComment("Some additional information");

        //items
        Items items = new Items();
        Items.Item item = new Items.Item();
        item.setPartNum("1");
        item.setProductName("Vodka");
        item.setQuantity(1);
        item.setUSPrice(new BigDecimal("10"));
        item.setComment("Russia, vodka, bear, balalayka");
        item.setShipDate(getXmlGregorianCalendar());

        items.getItem().add(item);
        purchaseOrder.setItems(items);

        return purchaseOrder;
    }

    public XMLGregorianCalendar getXmlGregorianCalendar() {
        XMLGregorianCalendar date = null;
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        try {
            date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return date;
    }
}
