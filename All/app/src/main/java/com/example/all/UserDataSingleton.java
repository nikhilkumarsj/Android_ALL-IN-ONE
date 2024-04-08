package com.example.all;
public class UserDataSingleton {
    private static final UserDataSingleton instance = new UserDataSingleton();

    private String userId;
    private String roleId;
    private String location;
    private String locationId;
    private String name;



    private String bunkID;
    private String phoneNo;

    // Private constructor to prevent instantiation from other classes
    private UserDataSingleton() {}

    // Getter method to access the singleton instance
    public static UserDataSingleton getInstance() {
        return instance;
    }

    // Add your methods and properties here

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUsername() {
        return name;
    }

    public void setUsername(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getBunkID() {
        return bunkID;
    }

    public void setBunkID(String bunkID) {
        this.bunkID = bunkID;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

}
