package ru.ekabardinsky.magister.camel.spring.boot.comparison;

import org.apache.camel.component.cxf.CxfComponent;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.DataFormat;
import org.apache.camel.spi.RestConsumerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.ekabardinsky.magister.camel.spring.boot.comparison.processor.TestCasesPropertiesProcessor;
import ru.ekabardinsky.magister.commons.Monitoring.ResourcesUsageMonitor;
import ru.ekabardinsky.magister.commons.soap.service.InboundService;
import ru.ekabardinsky.magister.soap.service.PurchaseOrder;

import java.lang.reflect.InvocationTargetException;

@SpringBootApplication
public class ComparisonApplication {
    @Value("${soap.serviceAddress}")
    private String soapServiceAddress;

    @Value("${monitoring.sleep}")
    private Long monitoringSleep;

    @Value("${monitoring.bufferSize}")
    private Integer monitoringBufferSize;

    @Bean
    public TestCasesPropertiesProcessor testCasesPropertiesProcessor() {
        return new TestCasesPropertiesProcessor();
    }

    @Bean
    public ResourcesUsageMonitor resourcesUsageMonitor() {
        ResourcesUsageMonitor resourcesUsageMonitor = new ResourcesUsageMonitor(monitoringSleep, monitoringBufferSize);
        try {
            resourcesUsageMonitor.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourcesUsageMonitor;
    }

    @Bean
    public CxfEndpoint cxfEndpoint() {
        CxfEndpoint cxfEndpoint = new CxfEndpoint();
        cxfEndpoint.setDataFormat(DataFormat.POJO);
        cxfEndpoint.setWsdlURL(soapServiceAddress + "?wsdl");
        cxfEndpoint.setPortName("PurchaseOrderImplPort");
        cxfEndpoint.setAddress(soapServiceAddress);
        cxfEndpoint.setServiceClass(PurchaseOrder.class);
        cxfEndpoint.setDefaultOperationName("pay");

        return cxfEndpoint;
    }

    @Bean
    public CxfEndpoint cxfEndpointService() {
        CxfEndpoint cxfEndpoint = new CxfEndpoint();
        cxfEndpoint.setServiceClass(InboundService.class);
        cxfEndpoint.setAddress("/api/soap/inbound");

        return cxfEndpoint;
    }
}
