package com.example.all.ui.gallery;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {

    @POST("APPBycanInput")
    Call<List<ShipmentAPIResponse>> postData(@Body ShipmentPostModel PostData);

    @POST("UpdateDieselIndentStatus")
    Call<ResponseBody> SendData(@Body PostDataModel SendData);

}



