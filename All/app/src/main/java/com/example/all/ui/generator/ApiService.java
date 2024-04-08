package com.example.all.ui.generator;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("Driver")
    Call<List<Driver>> getDrivers();

    @GET("Vehicle")
    Call<List<Vehicle>> getVehicle();

    @GET("InternalLocation")
    Call<List<Location>> getLocation();

    // Define endpoint for submitting data (POST request)
    @POST("InternalTransferTransaction") // Replace with the actual endpoint f  or submitting data
    Call<ResponseBody> postData(@Body PostDataModel PostData);

//    @POST("AppLogin")
//    Call<ResponseBody> PassData(@Body LoginRequest loginRequest);
}
