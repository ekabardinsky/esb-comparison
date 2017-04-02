package ru.ekabardinsky.magister.commons.soap.service;

import org.tempuri.purchaseorderschema.PurchaseOrderType;

import javax.jws.WebService;

/**
 * Created by ekabardinsky on 4/2/17.
 */
@WebService
public class InboundService {
    public String test(PurchaseOrderType purchaseOrder) {
        return "Ok";
    }
}
