package com.example.a2048test3.database;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GameApi {

    @GET("/get_move_count")
    Call<MoveCountResponse> getMoveCount(@Query("username") String username);

    @POST("/update_move_count")
    Call<MoveCountResponse> updateMoveCount(@Body MoveCountRequest request);

    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest request);  // Dodanie metody logowania
    @POST("/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);  // Dodanie metody rejestracji
}