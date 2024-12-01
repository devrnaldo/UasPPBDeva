<?php
header('Content-Type: application/json');

// Koneksi ke database
include 'db_connection.php';

$query = "SELECT id, jenis_kecelakaan, deskripsi, latitude, longitude, photo, created_at FROM reports ORDER BY created_at DESC";
$result = $conn->query($query);

$reports = [];

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $reports[] = [
            'id' => $row['id'],
            'jenisKecelakaan' => $row['jenis_kecelakaan'],
            'deskripsi' => $row['deskripsi'],
            'latitude' => $row['latitude'],
            'longitude' => $row['longitude'],
            'photo' => $row['photo'],
            'created_at' => $row['created_at']
        ];
    }
}

echo json_encode(['reports' => $reports]);

$conn->close();
?>
