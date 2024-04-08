package com.example.all.ui.gallery;

public class ShipmentPostModel {
    String Date, IndentNumber,Bunk;

    public ShipmentPostModel(String date, String indentNumber, String bunk) {
        Date = date;
        IndentNumber = indentNumber;
        Bunk=bunk;

    }
    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getIndentNumber() {
        return IndentNumber;
    }

    public void setIndentNumber(String indentNumber) {
        IndentNumber = indentNumber;
    }

    public String getBunk() {
        return Bunk;
    }

    public void setBunk(String bunk) {
        this.Bunk = bunk;
    }
}
