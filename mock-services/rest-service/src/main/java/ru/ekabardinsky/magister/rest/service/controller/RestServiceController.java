package ru.ekabardinsky.magister.rest.service.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tempuri.purchaseorderschema.Items;
import org.tempuri.purchaseorderschema.PurchaseOrderType;
import org.tempuri.purchaseorderschema.USAddress;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by ekabardinsky on 2/23/17.
 */
@RestController
public class RestServiceController {

    private PurchaseOrderType purchaseOrder;

    public RestServiceController() {
        this.purchaseOrder = getPurchaseOrder();
    }

    @RequestMapping(method = POST, value = "/api/rest/post")
    public PurchaseOrderType post(@RequestBody String body) {
        return purchaseOrder;
    }

    public PurchaseOrderType getPurchaseOrder() {
        PurchaseOrderType purchaseOrderType = new PurchaseOrderType();
        purchaseOrderType.setComment("Some comment for purchase order");

        USAddress billTo = new USAddress();
        billTo.setCountry("RU");
        billTo.setCity("Tyumen");
        billTo.setStreet("Perekopskaya");
        billTo.setState("Pending");
        billTo.setZip(new BigDecimal("625003"));
        billTo.setName("Name of address");

        purchaseOrderType.setBillTo(billTo);
        purchaseOrderType.setShipTo(billTo); //because it is example
        purchaseOrderType.setConfirmDate(getCalendar());
        purchaseOrderType.setOrderDate(getCalendar());
        purchaseOrderType.setItems(getItems());

        return purchaseOrderType;
    }

    private Items getItems() {
        Items items = new Items();

        Items.Item item = new Items.Item();
        item.setComment("Comment for item");
        item.setPartNum("8");
        item.setProductName("Pizza");
        item.setQuantity(1);
        item.setShipDate(getCalendar());
        item.setUSPrice(new BigDecimal("10"));

        items.getItem().add(item);

        return items;
    }

    private XMLGregorianCalendar getCalendar() {
        try {
            GregorianCalendar gregory = new GregorianCalendar();
            gregory.setTime(new Date());
            return DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(
                            gregory);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            throw new IllegalStateException("Expect XMLGregorianCalendar instance should be created");
        }
    }
}
