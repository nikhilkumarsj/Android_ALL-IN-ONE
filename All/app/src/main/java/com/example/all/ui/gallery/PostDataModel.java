package com.example.all.ui.gallery;

public class PostDataModel {


    private String IndentID;
    private String Accept;

    public PostDataModel(String indentID, String accept) {
        IndentID = indentID;
        Accept = accept;
    }

    public  String getIndentID() {
        return IndentID;
    }

    public void setIndentID(String indentID) {
        IndentID = indentID;
    }

    public String getAccept() {
        return Accept;
    }

    public void setAccept(String accept) {
        Accept = accept;
    }

}
