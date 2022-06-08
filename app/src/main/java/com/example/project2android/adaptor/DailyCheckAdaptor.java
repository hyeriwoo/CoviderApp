package com.example.project2android.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2android.R;
import com.example.project2android.object.DailyCheck;

import java.util.ArrayList;

public class DailyCheckAdaptor extends RecyclerView.Adapter<DailyCheckAdaptor.ViewHolder> {
    Context context;
    ArrayList<DailyCheck> dailyChecks;

    public DailyCheckAdaptor(Context context, ArrayList<DailyCheck> dailyChecks) {
        this.context = context;
        this.dailyChecks = dailyChecks;
    }

    @NonNull
    @Override
    public DailyCheckAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.dailycheck_item, parent, false);
        return new DailyCheckAdaptor.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyCheck dailyCheck = dailyChecks.get(position);
        holder.HealthStatus.setText(dailyCheck.getStringHealthStatus());
        holder.TestTime.setText(dailyCheck.getStringDate());
        if (dailyCheck.getHealthStatus() == 0) {
            holder.HealthStatus.setTextColor(0xff006600);
        } else if (dailyCheck.getHealthStatus() == 1 || dailyCheck.getHealthStatus() == 2) {
            holder.HealthStatus.setTextColor(0xff990000);
        }
    }

    @Override
    public int getItemCount() {
        return dailyChecks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView HealthStatus, TestTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            HealthStatus = itemView.findViewById(R.id.HealthStatus);
            TestTime = itemView.findViewById(R.id.TestTime);
        }

    }
}
