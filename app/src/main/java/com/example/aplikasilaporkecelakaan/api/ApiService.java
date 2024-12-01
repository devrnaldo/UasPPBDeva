package com.example.aplikasilaporkecelakaan.api;

import com.example.aplikasilaporkecelakaan.models.Report;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface ApiService {
    @Headers({"Content-Type: application/json"})
    @POST("save_report.php")
    Call<ApiResponse> saveReport(@Body Report report);
    @GET("get_report.php") // Endpoint untuk mengambil laporan
    Call<ApiResponse> getReports();
}
