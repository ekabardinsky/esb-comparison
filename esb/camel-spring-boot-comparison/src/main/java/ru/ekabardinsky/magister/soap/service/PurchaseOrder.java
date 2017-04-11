package ru.ekabardinsky.magister.soap.service;

import org.tempuri.purchaseorderschema.PurchaseOrderType;

import javax.jws.WebService;

@WebService
public interface PurchaseOrder {
    PurchaseOrderType pay(PurchaseOrderType purchaseOrder);
}

