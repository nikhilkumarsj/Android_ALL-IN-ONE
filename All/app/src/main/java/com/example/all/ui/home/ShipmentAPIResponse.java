package com.example.all.ui.home;

import com.google.gson.annotations.SerializedName;

public class ShipmentAPIResponse {
    @SerializedName("ShipmentMonth")
    private final String ShipmentMonth;

    @SerializedName("ShipmentNo")
    private final String ShipmentNo;

    @SerializedName("GcNo")
    private final String GcNo;
    @SerializedName("ShipmentDate")
    private final String ShipmentDate;
    @SerializedName("VehNo")
    private final String VehNo;
    @SerializedName("Qty")
    private final String Qty;
    @SerializedName("Product")
    private final String Product;

    @SerializedName("PartyName")
    private final String PartyName;
    @SerializedName("From")
    private final String From;
    @SerializedName("To")
    private final String To;
    @SerializedName("TransportName")
    private final String TransportName;

    public String getShipmentMonth() {
        return ShipmentMonth;
    }

    public String getShipmentNo() {
        return ShipmentNo;
    }

    public String getGcNo() {
        return GcNo;
    }

    public String getShipmentDate() {
        return ShipmentDate;
    }

    public String getVehNo() {
        return VehNo;
    }

    public String getQty() {
        return Qty;
    }

    public String getProduct() {
        return Product;
    }

    public String getPartyName() {
        return PartyName;
    }

    public String getFrom() {
        return From;
    }

    public String getTo() {
        return To;
    }

    public String getTransportName() {
        return TransportName;
    }

    public ShipmentAPIResponse(String shipmentMonth, String shipmentNo, String gcNo, String shipmentDate, String vehNo, String qty, String product, String partyName, String from, String to, String transportName) {
        ShipmentMonth = shipmentMonth;
        ShipmentNo = shipmentNo;
        GcNo = gcNo;
        ShipmentDate = shipmentDate;
        VehNo = vehNo;
        Qty = qty;
        Product = product;
        PartyName = partyName;
        From = from;
        To = to;
        TransportName = transportName;
    }
}
