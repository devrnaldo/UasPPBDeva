package com.example.aplikasilaporkecelakaan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Base64;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.aplikasilaporkecelakaan.api.ApiClient;
import com.example.aplikasilaporkecelakaan.api.ApiResponse;
import com.example.aplikasilaporkecelakaan.api.ApiService;
import com.example.aplikasilaporkecelakaan.models.Report;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.ArrayAdapter;

public class LaporKecelakaanActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_LOCATION_PERMISSION = 100;

    private Spinner jenisKecelakaanSpinner;
    private EditText deskripsiEditText;
    private ImageView imageView;
    private Button submitButton;
    private Button takePhotoButton;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private double latitude = 0.0;
    private double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapor_kecelakaan);

        // Inisialisasi elemen UI
        jenisKecelakaanSpinner = findViewById(R.id.spinnerJenisKecelakaan);
        deskripsiEditText = findViewById(R.id.etDeskripsi);
        imageView = findViewById(R.id.imageView);
        submitButton = findViewById(R.id.kirimFormButton);
        takePhotoButton = findViewById(R.id.unggahFotoButton);

        // Setup Spinner dengan ArrayAdapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.jenis_kecelakaan_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jenisKecelakaanSpinner.setAdapter(adapter);

        // Inisialisasi FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Tombol untuk mengambil foto
        takePhotoButton.setOnClickListener(v -> takePhoto());

        // Tombol untuk submit form
        submitButton.setOnClickListener(v -> {
            getLocationAndSubmitReport();
        });
    }

    private void takePhoto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }

    private void getLocationAndSubmitReport() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<android.location.Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    latitude = task.getResult().getLatitude();
                    longitude = task.getResult().getLongitude();
                    submitReport();
                } else {
                    Toast.makeText(LaporKecelakaanActivity.this, "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void submitReport() {
        String jenisKecelakaan = jenisKecelakaanSpinner.getSelectedItem().toString();
        String deskripsi = deskripsiEditText.getText().toString();

        if (jenisKecelakaan.isEmpty() || deskripsi.isEmpty()) {
            Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        imageView.setDrawingCacheEnabled(true);
        Bitmap photo = imageView.getDrawingCache();
        String photoBase64 = encodeImageToBase64(photo);

        Log.d("DEBUG_APP", "Jenis Kecelakaan: " + jenisKecelakaan);
        Log.d("DEBUG_APP", "Deskripsi: " + deskripsi);
        Log.d("DEBUG_APP", "Latitude: " + latitude);
        Log.d("DEBUG_APP", "Longitude: " + longitude);

        sendReport(jenisKecelakaan, deskripsi, photoBase64);
    }

    private String encodeImageToBase64(Bitmap photo) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void sendReport(String jenisKecelakaan, String deskripsi, String photoBase64) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse> call = apiService.saveReport(new Report(jenisKecelakaan, deskripsi, photoBase64, latitude, longitude));

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LaporKecelakaanActivity.this, "Laporan berhasil dikirim!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LaporKecelakaanActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(LaporKecelakaanActivity.this, "Gagal mengirim laporan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(LaporKecelakaanActivity.this, "Gagal mengirim laporan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
