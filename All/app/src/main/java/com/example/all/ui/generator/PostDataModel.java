package com.example.all.ui.generator;

import com.google.gson.annotations.SerializedName;

public class PostDataModel {
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

    public PostDataModel(String driverName, String date, String vehicleNo, String from, String to, String coilID, String tonnage, String pageRefNo, String remarks) {
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCoilID() {
        return coilID;
    }

    public void setCoilID(String coilID) {
        this.coilID = coilID;
    }

    public String getTonnage() {
        return tonnage;
    }

    public void setTonnage(String tonnage) {
        this.tonnage = tonnage;
    }

    public String getPageRefNo() {
        return pageRefNo;
    }

    public void setPageRefNo(String pageRefNo) {
        this.pageRefNo = pageRefNo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
