package com.example.all;

import com.example.all.ui.home.ShipmentAPIResponse;
import com.example.all.ui.home.ShipmentPostModel;
import com.example.all.ui.slideshow.GCAPIResponse;
import com.example.all.ui.slideshow.GCPostModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {
    @POST("SearchOnDateforShipmentDetails") // Replace with the actual endpoint for submitting data
    Call<List<ShipmentAPIResponse>> postData(@Body ShipmentPostModel PostData);

    @POST("SearchonDateforRakeGCDetails") // Replace with the actual endpoint for submitting data
    Call<List<GCAPIResponse>> postGCData(@Body GCPostModel PostData);

    @POST("AppLogin")
    Call<ResponseBody> PassData(@Body LoginRequest loginRequest);
}

