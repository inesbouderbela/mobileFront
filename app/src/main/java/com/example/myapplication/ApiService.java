package com.example.myapplication;

import com.example.myapplication.Model.Seance;
import com.example.myapplication.Model.Spectacle;
import com.example.myapplication.Model.TopSpectacle;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface ApiService {
    @GET("spectacles/next-month")
    Call<List<Spectacle>> getSpectacles();
    @GET("spectacles/topspectacles")
    Call<List<TopSpectacle>> getTopSpectacles();
    @GET("spectacles/spectaclesAfterToday")
    Call<List<TopSpectacle>> getAllSpectacles();
    @GET("spectacles/seance/{seanceId}")
    Call<Seance> getSeanceDetails(@Path("seanceId") Long seanceId);
}
