package com.example.all.ui.internal;

public class PostDataModel {
private String RakeShipmentID;
    private String VehicleID;
    private String DriverID;
    private String Unload;
    private String IssuedBy;

    public PostDataModel(String rakeShipmentID, String vehicleID, String driverID, String unload,String issuedBy) {
        RakeShipmentID = rakeShipmentID;
        VehicleID = vehicleID;
        DriverID = driverID;
        Unload = unload;
        IssuedBy= issuedBy;
    }

    public String getRakeShipmentID() {
        return RakeShipmentID;
    }

    public void setRakeShipmentID(String rakeShipmentID) {
        RakeShipmentID = rakeShipmentID;
    }

    public String getVehicleID() {
        return VehicleID;
    }

    public void setVehicleID(String vehicleID) {
        VehicleID = vehicleID;
    }

    public String getDriverID() {
        return DriverID;
    }

    public void setDriverID(String driverID) {
        DriverID = driverID;
    }

    public String getUnload() {
        return Unload;
    }

    public void setUnload(String unload) {
        Unload = unload;
    }
    public String getIssuedBy() { return IssuedBy; }

    public void setIssuedBy(String issuedBy) { IssuedBy = issuedBy; }
}
