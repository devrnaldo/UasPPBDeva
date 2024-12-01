package com.example.aplikasilaporkecelakaan.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Report {
    private int id;
    private String jenisKecelakaan;
    private String deskripsi;
    private String photo;
    private double latitude;
    private double longitude;
    private String created_at;

    public Report(String jenisKecelakaan, String deskripsi, String photo, double latitude, double longitude) {
        this.jenisKecelakaan = jenisKecelakaan;
        this.deskripsi = deskripsi;
        this.photo = photo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getter untuk created_at yang mengubah format tanggal
    public String getFormattedCreatedAt() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = inputFormat.parse(created_at);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");  // Format baru
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return created_at;  // Kembalikan string original jika parsing gagal
        }
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getCreated_at() {
        return created_at;
    }

    // Getter untuk jenis kecelakaan
    public String getJenisKecelakaan() {
        return jenisKecelakaan;
    }

    // Setter untuk jenis kecelakaan
    public void setJenisKecelakaan(String jenisKecelakaan) {
        this.jenisKecelakaan = jenisKecelakaan;
    }

    // Getter untuk deskripsi
    public String getDeskripsi() {
        return deskripsi;
    }

    // Setter untuk deskripsi
    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    // Getter untuk foto dalam format Base64
    public String getPhoto() {
        return photo;
    }

    // Setter untuk foto dalam format Base64
    public void setPhotoBase64(String photo) {
        this.photo = photo;
    }

    // Getter untuk latitude
    public double getLatitude() {
        return latitude;
    }

    // Setter untuk latitude
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    // Getter untuk longitude
    public double getLongitude() {
        return longitude;
    }

    // Setter untuk longitude
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
