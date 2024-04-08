package com.example.all.ui.generator;

import com.google.gson.annotations.SerializedName;

public class SubmitRequestBody {
    @SerializedName("driverName")
    private String driverName;

    @SerializedName("date")
    private String date;

    @SerializedName("vehicleNo")
    private String vehicleNo;

    @SerializedName("from")
    private String from;

    @SerializedName("to")
    private String to;

    @SerializedName("coilID")
    private String coilID;

    @SerializedName("tonnage")
    private String tonnage;

    @SerializedName("pageRefNo")
    private String pageRefNo;

    @SerializedName("remarks")
    private String remarks;

    public SubmitRequestBody(String driverName, String date, String vehicleNo, String from, String to, String coilID, String tonnage, String pageRefNo, String remarks) {
        this.driverName = driverName;
        this.date = date;
        this.vehicleNo = vehicleNo;
        this.from = from;
        this.to = to;
        this.coilID = coilID;
        this.tonnage = tonnage;
        this.pageRefNo = pageRefNo;
        this.remarks = remarks;
    }
}
