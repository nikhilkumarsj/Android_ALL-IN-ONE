package com.example.all.ui.internal;


import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIService {

    @GET("Driver")
    Call<List<Driver>> getDrivers();

    @GET("Vehicle")
    Call<List<Vehicle>> getVehicle();

    @GET("Unload")
    Call<List<Location>> getLocation();

    @POST("GetGCDetails")
    Call<List<ShipmentAPIResponse>> postData(@Body ShipmentPostModel PostData);

    @POST("GCGroup")
    Call<ResponseBody> SendData(@Body PostDataModel SendData);

//    @POST("AppLogin")
//    Call<ResponseBody> PassData(@Body LoginRequest loginRequest);
}
