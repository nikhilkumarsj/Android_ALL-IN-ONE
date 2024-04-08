package com.example.all.ui.gallery;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ShipmentAPIResponse implements Parcelable {
    @SerializedName("IndentID")
    private final String IndentID;
    @SerializedName("IndentNo")
    private final String IndentNo;
    @SerializedName("Inddate")
    private final String Inddate;
    @SerializedName("VehicleNo")
    private final String VehicleNo;
    @SerializedName("DriverName")
    private final String DriverName;
    @SerializedName("IssuedBy")
    private final String IssuedBy;
    @SerializedName("HSDLeters")
    private final String HSDLeters;
    @SerializedName("HSDAmount")
    private final String HSDAmount;
    @SerializedName("Remarks")
    private final String Remarks;

    public String getIndentID() {
        return IndentID;
    }

    public String getIndentNo() {
        return IndentNo;
    }

    public String getInddate() {
        return Inddate;
    }

    public String getVehicleNo() {
        return VehicleNo;
    }

    public String getDriverName() {
        return DriverName;
    }

    public String getIssuedBy() {
        return IssuedBy;
    }

    public String getHSDLeters() {
        return HSDLeters;
    }

    public String getHSDAmount() {
        return HSDAmount;
    }

    public String getRemarks() {
        return Remarks;
    }

    public ShipmentAPIResponse(String indentID, String indentNo, String inddate, String vehicleNo, String driverName, String issuedBy, String HSDLeters, String HSDAmount, String remarks) {
        IndentID = indentID;
        IndentNo = indentNo;
        Inddate = inddate;
        VehicleNo = vehicleNo;
        DriverName = driverName;
        IssuedBy = issuedBy;
        this.HSDLeters = HSDLeters;
        this.HSDAmount = HSDAmount;
        Remarks = remarks;
    }

    protected ShipmentAPIResponse(Parcel in) {
        IndentID = in.readString();
        IndentNo = in.readString();
        Inddate = in.readString();
        VehicleNo = in.readString();
        DriverName = in.readString();
        IssuedBy = in.readString();
        HSDLeters = in.readString();
        HSDAmount = in.readString();
        Remarks = in.readString();
        }

    public static final Creator<ShipmentAPIResponse> CREATOR = new Creator<ShipmentAPIResponse>() {
        @Override
        public ShipmentAPIResponse createFromParcel(Parcel in) {
            return new ShipmentAPIResponse(in);
        }

        @Override
        public ShipmentAPIResponse[] newArray(int size) {
            return new ShipmentAPIResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(IndentID);
        dest.writeString(IndentNo);
        dest.writeString(Inddate);
        dest.writeString(VehicleNo);
        dest.writeString(DriverName);
        dest.writeString(IssuedBy);
        dest.writeString(HSDLeters);
        dest.writeString(HSDAmount);
        dest.writeString(Remarks);
    }

}
