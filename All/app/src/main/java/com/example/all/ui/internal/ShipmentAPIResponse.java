package com.example.all.ui.internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ShipmentAPIResponse implements Parcelable {
    @SerializedName("RakeShipmentID")
    private final String RakeShipmentID;

    @SerializedName("WagonNo")
    private final String WagonNo;

    @SerializedName("Material")
    private final String Material;
    @SerializedName("ProductName")
    private final String ProductName;
    @SerializedName("NetWt")
    private final String NetWt;
    @SerializedName("InvoiceNo")
    private final String InvoiceNo;

    @SerializedName("DestinationName")
    private final String DestinationName;

    @SerializedName("PartyName")
    private final String PartyName;

    @SerializedName("Rake")
    private final String Rake;

    public String getRakeShipmentID() {
        return RakeShipmentID;
    }

    public String getWagonNo() {
        return WagonNo;
    }

    public String getMaterial() {
        return Material;
    }

    public String getProductName() {
        return ProductName;
    }

    public String getNetWt() {
        return NetWt;
    }

    public String getInvoiceNo() {
        return InvoiceNo;
    }



    public String getDestinationName() {
        return DestinationName;
    }



    public String getPartyName() {
        return PartyName;
    }

    public String getRake() {
        return Rake;
    }



    public ShipmentAPIResponse(String rakeShipmentID, String wagonNo, String material, String productName, String netWt, String invoiceNo, String destinationName, String partyName, String rake) {
        RakeShipmentID = rakeShipmentID;
        WagonNo = wagonNo;
        Material = material;
        ProductName = productName;
        NetWt = netWt;
        InvoiceNo = invoiceNo;

        DestinationName = destinationName;
        PartyName = partyName;
        Rake = rake;
    }

    protected ShipmentAPIResponse(Parcel in) {
        RakeShipmentID = in.readString();
        WagonNo = in.readString();
        Material = in.readString();
        ProductName = in.readString();
        NetWt = in.readString();
        InvoiceNo = in.readString();

        DestinationName = in.readString();

        PartyName = in.readString();
        Rake = in.readString();

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
        dest.writeString(RakeShipmentID);
        dest.writeString(WagonNo);
        dest.writeString(Material);
        dest.writeString(ProductName);
        dest.writeString(NetWt);
        dest.writeString(InvoiceNo);

        dest.writeString(DestinationName);

        dest.writeString(PartyName);
        dest.writeString(Rake);
    }
}
