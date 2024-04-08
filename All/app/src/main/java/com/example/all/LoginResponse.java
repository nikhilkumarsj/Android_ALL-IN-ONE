package com.example.all;

public class LoginResponse {


    private String UserId;
    private String RoleId;
    private String Location;
    private int LocationId;
    private String Name;
    private String BunkID;
    private String Phone_No;
    private int Column1;
    private int Column2;


    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getRoleId() {
        return RoleId;
    }

    public void setRoleId(String roleId) {
        RoleId = roleId;
    }
    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public int getLocationId() {
        return LocationId;
    }

    public void setLocationId(int locationId) {
        LocationId = locationId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
    public String getBunkID() {
        return BunkID;
    }
    public void setBunkID(String bunkID) {
        BunkID = bunkID;
    }
    public String getPhone_No() {
        return Phone_No;
    }

    public void setPhone_No(String phone_No) {
        Phone_No = phone_No;
    }


    public LoginResponse(String userId, String roleId, String location, int locationId, String name, String bunkID, String phone_No, int column1, int column2) {
        UserId = userId;
        RoleId = roleId;
        Location = location;
        LocationId = locationId;
        Name = name;
        BunkID = bunkID;
        Phone_No = phone_No;
        Column1 = column1;
        Column2 = column2;
    }
    public int getColumn1() {
        return Column1;
    }

    public void setColumn1(int column1) {
        Column1 = column1;
    }
    public int getColumn2() {
        return Column2;
    }

    public void setColumn2(int column2) {
        Column2 = column2;
    }
}
