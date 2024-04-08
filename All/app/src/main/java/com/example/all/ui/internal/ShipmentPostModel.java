package com.example.all.ui.internal;

public class ShipmentPostModel {
    String Material,InvoiceNo;

    public ShipmentPostModel(String Material, String InvoiceNo) {
        this.Material = Material;
        this.InvoiceNo = InvoiceNo;
    }

    public String getMaterial() {
        return Material;
    }

    public void setMaterial(String material) {
        this.Material = material;
    }

    public String getInvoiceNo() {
        return InvoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.InvoiceNo = invoiceNo;
    }
}
