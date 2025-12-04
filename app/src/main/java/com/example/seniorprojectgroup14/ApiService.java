package com.example.seniorprojectgroup14;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
// Import your new POJO classes

public interface ApiService {

    @POST("register") // ⬅️ Removed the leading slash
    Call<RegisterResponse> register(@Body RegisterRequest request); // ⬅️ Switched to POJO classes
}