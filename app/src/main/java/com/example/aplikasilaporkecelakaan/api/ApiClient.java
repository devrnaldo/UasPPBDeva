package com.example.aplikasilaporkecelakaan.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ApiClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.1.8/laporkecelakaan/";  // Pastikan alamat IP sesuai dengan server Anda

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Membuat Gson dengan setLenient
            Gson gson = new GsonBuilder()
                    .setLenient()  // Menambahkan setLenient agar bisa menerima JSON yang tidak valid
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // Gunakan Gson yang telah dimodifikasi
                    .build();
        }
        return retrofit;
    }
}
