package com.example.aplikasilaporkecelakaan;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.example.aplikasilaporkecelakaan.api.ApiClient;
import com.example.aplikasilaporkecelakaan.api.ApiResponse;
import com.example.aplikasilaporkecelakaan.api.ApiService;
import com.example.aplikasilaporkecelakaan.models.Report;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private double currentLatitude;
    private double currentLongitude;
    private Button btnZoomIn, btnZoomOut;
    private Map<Marker, Report> markerReportMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Layout hanya untuk peta

        // Inisialisasi map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        ImageButton btnZoomIn = findViewById(R.id.btnZoomIn);
        ImageButton btnZoomOut = findViewById(R.id.btnZoomOut);

        // Menambahkan listener untuk tombol zoom-in
        btnZoomIn.setOnClickListener(v -> {
            if (mMap != null) {
                float zoomLevel = mMap.getCameraPosition().zoom;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target, zoomLevel + 1.5f));
            }
        });

        // Menambahkan listener untuk tombol zoom-out
        btnZoomOut.setOnClickListener(v -> {
            if (mMap != null) {
                float zoomLevel = mMap.getCameraPosition().zoom;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target, zoomLevel - 1.5f));
            }
        });

        // Inisialisasi lokasi
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Tombol untuk membuka form laporan kecelakaan
        findViewById(R.id.btnLaporKecelakaan).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LaporKecelakaanActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Ambil lokasi terkini
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                // Tambahkan marker lokasi saat ini
                LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("Lokasi Anda"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        });

        // Panggil API untuk mendapatkan daftar laporan kecelakaan
        fetchReports();

        // Set listener untuk marker
        mMap.setOnMarkerClickListener(marker -> {
            Report report = markerReportMap.get(marker);
            if (report != null) {
                showReportDialog(report);
            }
            return false;
        });
    }

    private void fetchReports() {
        ApiService ApiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse> call = ApiService.getReports();

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Report> reports = response.body().getReports();
                    addMarkers(reports);
                } else {
                    Log.e("DEBUG_APP", "Gagal mendapatkan laporan: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("DEBUG_APP", "Error: " + t.getMessage());
            }
        });
    }

    private void addMarkers(List<Report> reports) {
        for (Report report : reports) {
            LatLng location = new LatLng(report.getLatitude(), report.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(report.getJenisKecelakaan())
                    .snippet(report.getDeskripsi()));
            markerReportMap.put(marker, report);
        }
    }

    private void showReportDialog(Report report) {
        // Membuat dialog builder dengan context
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_report, null);
        builder.setView(dialogView);

        // Inisialisasi elemen UI dalam dialog
        TextView tvJenisKecelakaan = dialogView.findViewById(R.id.tvJenisKecelakaan);
        TextView tvDeskripsi = dialogView.findViewById(R.id.tvDeskripsi);
        TextView tvTanggal = dialogView.findViewById(R.id.tvTanggal);
        ImageView ivFoto = dialogView.findViewById(R.id.ivFoto);

        // Set data ke view
        tvJenisKecelakaan.setText(report.getJenisKecelakaan());
        tvDeskripsi.setText(report.getDeskripsi());
        tvTanggal.setText(report.getCreated_at());  // Anda bisa format tanggal jika perlu

        // Cek apakah foto ada
        if (report.getPhoto() != null && !report.getPhoto().isEmpty()) {
            try {
                // Decode base64 image
                byte[] decodedString = Base64.decode(report.getPhoto(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivFoto.setImageBitmap(decodedByte);
                Log.d("DEBUG", "Base64 Image: " + report.getPhoto());
            } catch (Exception e) {
                e.printStackTrace();
                // Tampilkan gambar default atau error jika ada masalah dengan Base64
                ivFoto.setImageResource(R.drawable.default_image);  // Gambar default
            }
        } else {
            // Tampilkan gambar default jika foto tidak ada
            ivFoto.setImageResource(R.drawable.default_image);
        }

        // Menampilkan dialog dengan penyesuaian style
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        // Tombol kustom
        Button btnTutup = dialogView.findViewById(R.id.btnTutup);
        btnTutup.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
