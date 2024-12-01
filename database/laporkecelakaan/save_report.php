<?php
header('Content-Type: application/json');
include 'db_connection.php';

// Ambil data POST
$data = json_decode(file_get_contents("php://input"));

// Debug: Periksa apakah data diterima
if (!$data) {
    echo json_encode(["message" => "Tidak ada data yang diterima."]);
    exit;
}

// Periksa apakah data lengkap
if (
    isset($data->jenisKecelakaan) &&
    isset($data->deskripsi) &&
    isset($data->photo) &&
    isset($data->latitude) &&
    isset($data->longitude)
) {
    $jenisKecelakaan = $data->jenisKecelakaan;
    $deskripsi = $data->deskripsi;
    $photo = $data->photo;
    $latitude = $data->latitude;
    $longitude = $data->longitude;

    // SQL query untuk memasukkan data ke database
    $query = "INSERT INTO reports (photo, latitude, longitude, jenis_kecelakaan, deskripsi, created_at) 
              VALUES ('$photo', '$latitude', '$longitude', '$jenisKecelakaan', '$deskripsi', NOW())";

    if (mysqli_query($conn, $query)) {
        echo json_encode(["message" => "Laporan berhasil dikirim"]);
    } else {
        echo json_encode(["message" => "Gagal mengirim laporan", "error" => mysqli_error($conn)]);
    }
} else {
    echo json_encode(["message" => "Data yang dikirim tidak lengkap."]);
}
?>
