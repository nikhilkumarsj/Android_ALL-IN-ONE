package com.example.all.ui.internal;

public class SelectedItemInfo {
    private String rakeShipmentID;
    private String material;
    private String invoiceNo;

    public SelectedItemInfo(String rakeShipmentID, String material, String invoiceNo) {
        this.rakeShipmentID = rakeShipmentID;
        this.material = material;
        this.invoiceNo = invoiceNo;
    }

    public String getRakeShipmentID() {
        return rakeShipmentID;
    }

    public String getMaterial() {
        return material;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }
}
