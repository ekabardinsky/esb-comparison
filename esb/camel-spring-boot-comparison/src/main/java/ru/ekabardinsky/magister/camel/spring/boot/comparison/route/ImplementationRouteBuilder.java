package ru.ekabardinsky.magister.camel.spring.boot.comparison.route;

import org.apache.camel.Exchange;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spring.boot.FatJarRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tempuri.purchaseorderschema.Items;
import org.tempuri.purchaseorderschema.PurchaseOrderType;
import org.tempuri.purchaseorderschema.USAddress;
import ru.ekabardinsky.magister.camel.spring.boot.comparison.processor.TestCasesPropertiesProcessor;
import ru.ekabardinsky.magister.commons.Monitoring.ResourceManager;
import ru.ekabardinsky.magister.commons.Monitoring.ResourcesUsageMonitor;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by ekabardinsky on 4/11/17.
 */
@Component
public class ImplementationRouteBuilder extends FatJarRouter {
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
    private ResourcesUsageMonitor resourcesUsageMonitor;

    public ImplementationRouteBuilder() {
        this.examplePurchaseOrder = getExample();
    }

    @Override
    public void configure() {
        from("direct:testcases")
                .process(testCasesPropertiesProcessor)
                .setBody().groovy("resource:classpath:getTestCases.groovy");

        from("direct:monitor.start")
                .process(x -> resourcesUsageMonitor.start());

        from("direct:monitor.stop")
                .process(x -> resourcesUsageMonitor.stop())
                .process(x -> x.getOut().setBody(resourcesUsageMonitor.getResult()));

        from("direct:rest.inbound")
                .setBody(constant(null));

        from("direct:rest.outbound")
                .removeHeaders("*")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .to("http://" + restHost +
                        ":" + restPort +
                        restBasePath + "/rest/post")
                .unmarshal().json(JsonLibrary.Gson);

        from("direct:soap.outbound")
                .removeHeaders("*")
                .setBody(constant(examplePurchaseOrder))
                .to("cxf:bean:cxfEndpoint")
                .transform(simple("${body[0]}"))
                .marshal().jacksonxml();

        from("direct:ftp.outbound")
                .removeHeaders("*")
                .setBody(constant(ResourceManager.getTestResource(5000000)))
                .setHeader("CamelFileName", constant("5mb"))
                .to("ftp://" + ftpUsername +
                        "@" + ftpHost +
                        ":" + ftpPort + ftpPath +
                        "?password=" + ftpPassword +
                        "&binary=true")
                .setBody(constant(null));

        from("direct:soap.inbound")
                .setBody(constant(""));
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
