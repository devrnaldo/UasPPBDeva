package com.example.aplikasilaporkecelakaan.api;

import com.example.aplikasilaporkecelakaan.models.Report;

import java.util.List;

public class ApiResponse {
    private String status;
    private String message;

    private List<Report> reports;

    public List<Report> getReports() {
        return reports;
    }

    // Getter dan Setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

