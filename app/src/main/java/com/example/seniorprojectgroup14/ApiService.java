package com.example.seniorprojectgroup14;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/register")
    Call<JsonObject> register(@Body JsonObject body);
}