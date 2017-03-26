
package ru.ekabardinsky.magister.soap.service;

import org.tempuri.purchaseorderschema.PurchaseOrderType;

import javax.jws.WebService;

@WebService(endpointInterface = "ru.ekabardinsky.magister.soap.service.PurchaseOrder")
public class PurchaseOrderImpl implements PurchaseOrder {
    @Override
    public PurchaseOrderType pay(PurchaseOrderType purchaseOrder) {
        return purchaseOrder;
    }
}

