package com.example.aplikasilaporkecelakaan.models;

// ReportAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikasilaporkecelakaan.R;

import java.util.List;

import androidx.annotation.NonNull;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private List<Report> reportList;

    public ReportAdapter(List<Report> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.textJenisKecelakaan.setText(report.getJenisKecelakaan());
        holder.textDeskripsi.setText(report.getDeskripsi());
        holder.textCreatedAt.setText(report.getCreated_at());
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textJenisKecelakaan, textDeskripsi, textCreatedAt;

        public ViewHolder(View view) {
            super(view);
            textJenisKecelakaan = view.findViewById(R.id.textJenisKecelakaan);
            textDeskripsi = view.findViewById(R.id.textDeskripsi);
            textCreatedAt = view.findViewById(R.id.textCreatedAt);
        }
    }
}

