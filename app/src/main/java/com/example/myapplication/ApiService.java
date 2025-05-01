package com.example.myapplication;

import com.example.myapplication.Model.DateWithCount;
import com.example.myapplication.Model.Seance;
import com.example.myapplication.Model.Spectacle;
import com.example.myapplication.Model.TopSpectacle;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.time.LocalDate;
import java.util.List;

public interface ApiService {
    @GET("spectacles/recommended")
    Call<List<Spectacle>> getSpectacles();
    @GET("spectacles/topspectacles")
    Call<List<TopSpectacle>> getTopSpectacles();
    @GET("spectacles/spectaclesAfterToday")
    Call<List<TopSpectacle>> getAllSpectacles();
    @GET("spectacles/seance/{seanceId}")
    Call<Seance> getSeanceDetails(@Path("seanceId") Long seanceId);
    @GET("spectacles/spectaclesAfterToday/{category}")
    Call<List<TopSpectacle>> getSpectaclesByCategory(@Path("category") String category);

    @GET("spectacles/topspectacles/{category}")
    Call<List<TopSpectacle>> getTopSpectaclesByCategory(@Path("category") String category);

    @GET("seances/searchByName")
    Call<List<Seance>> getSeancesBySpectacleName(@Query("name") String spectacleName);
    @GET("spectacles/searchByName")
    Call<List<Seance>> searchSeancesByTitle(@Query("title") String title);
    @GET("spectacles/Dates")
    Call<List<DateWithCount>> getDatesWithEventCount();
    @GET("spectacles/searchByDate")
    Call<List<Seance>> getSeancesByDate(@Query("date") String date);
}
