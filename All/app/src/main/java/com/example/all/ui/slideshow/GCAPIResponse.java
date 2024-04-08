package com.example.all.ui.slideshow;

import com.google.gson.annotations.SerializedName;

public class GCAPIResponse {
    @SerializedName("RakeShipmentID")
    private String RakeShipmentID;

    @SerializedName("Material")
    private String Material;

    @SerializedName("Customer")
    private String Customer;
    @SerializedName("NetWt")
    private String NetWt;
    @SerializedName("InvoiceNo")
    private String InvoiceNo;
    @SerializedName("Rake")
    private String Rake;
    @SerializedName("ShipmentMonth")
    private String ShipmentMonth;

    @SerializedName("From")
    private String From;
    @SerializedName("To")
    private String To;
    @SerializedName("ShipmentDate")
    private String ShipmentDate;
    @SerializedName("TransportName")
    private String TransportName;
    @SerializedName("ProductName")
    private String ProductName;
    @SerializedName("WagonNo")
    private String WagonNo;

    public String getRakeShipmentID() {
        return RakeShipmentID;
    }

    public void setRakeShipmentID(String rakeShipmentID) {
        RakeShipmentID = rakeShipmentID;
    }

    public String getMaterial() {
        return Material;
    }

    public void setMaterial(String material) {
        Material = material;
    }

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public String getNetWt() {
        return NetWt;
    }

    public void setNetWt(String netWt) {
        NetWt = netWt;
    }

    public String getInvoiceNo() {
        return InvoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        InvoiceNo = invoiceNo;
    }

    public String getRake() {
        return Rake;
    }

    public void setRake(String rake) {
        Rake = rake;
    }

    public String getShipmentMonth() {
        return ShipmentMonth;
    }

    public void setShipmentMonth(String shipmentMonth) {
        ShipmentMonth = shipmentMonth;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getShipmentDate() {
        return ShipmentDate;
    }

    public void setShipmentDate(String shipmentDate) {
        ShipmentDate = shipmentDate;
    }

    public String getTransportName() {
        return TransportName;
    }

    public void setTransportName(String transportName) {
        TransportName = transportName;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getWagonNo() {
        return WagonNo;
    }

    public void setWagonNo(String wagonNo) {
        WagonNo = wagonNo;
    }

    public GCAPIResponse(String rakeShipmentID, String material, String customer, String netWt, String invoiceNo, String rake, String shipmentMonth, String from, String to, String shipmentDate, String transportName, String productName, String wagonNo) {
        RakeShipmentID = rakeShipmentID;
        Material = material;
        Customer = customer;
        NetWt = netWt;
        InvoiceNo = invoiceNo;
        Rake = rake;
        ShipmentMonth = shipmentMonth;
        From = from;
        To = to;
        ShipmentDate = shipmentDate;
        TransportName = transportName;
        ProductName = productName;
        WagonNo = wagonNo;
    }
}
