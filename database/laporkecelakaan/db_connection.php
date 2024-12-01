<?php
// db_connection.php

$host = "localhost";      // Ganti dengan host database Anda, jika berbeda
$user = "root";           // Ganti dengan username database Anda
$password = "";           // Ganti dengan password database Anda
$dbname = "laporkecelakaan"; // Nama database Anda

// Membuat koneksi ke database
$conn = new mysqli($host, $user, $password, $dbname);

// Mengecek koneksi
if ($conn->connect_error) {
    die("Koneksi gagal: " . $conn->connect_error);
}
?>
