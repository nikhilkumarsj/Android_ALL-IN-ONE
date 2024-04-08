package com.example.all.ui.home;

public class ShipmentPostModel {
    String From,To;

    public ShipmentPostModel(String from, String to) {
        From = from;
        To = to;
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
}
