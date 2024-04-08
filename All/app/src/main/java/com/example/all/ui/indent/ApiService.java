package com.example.all.ui.indent;

import com.example.all.ui.generator.Driver;
import com.example.all.ui.generator.Location;
import com.example.all.ui.generator.PostDataModel;
import com.example.all.ui.generator.Vehicle;

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

}
