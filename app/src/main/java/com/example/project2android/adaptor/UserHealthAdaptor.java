package com.example.project2android.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2android.R;
import com.example.project2android.object.User;

import java.util.ArrayList;

public class UserHealthAdaptor extends RecyclerView.Adapter<UserHealthAdaptor.ViewHolder> {
    Context context;
    ArrayList<User> students;
    RecyclerClickListener listener;

    public UserHealthAdaptor(Context context, ArrayList<User> students, RecyclerClickListener listener) {
        this.context = context;
        this.students = students;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserHealthAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHealthAdaptor.ViewHolder holder, int position) {
        User student = students.get(position);
        holder.StudentName.setText(student.getName());
        holder.StudentHealth.setText(student.getRecentHealth());
        holder.StudentTestTime.setText(student.getRecentTestTime());
        if (student.getRecentHealth().equals("Positive")) {
            holder.StudentHealth.setTextColor(0xff990000);
        } else if (student.getRecentHealth().equals("Symptoms")) {
            holder.StudentHealth.setTextColor(0xff990000);
        } else if (student.getRecentHealth().equals("Negative")) {
            holder.StudentHealth.setTextColor(0xff006600);
        }
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public interface RecyclerClickListener {
        void onClick(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView StudentName, StudentHealth, StudentTestTime;
        RecyclerClickListener onNoteListener;

        public ViewHolder(@NonNull View itemView, RecyclerClickListener onNoteListener) {
            super(itemView);
            StudentName = itemView.findViewById(R.id.studentName);
            StudentHealth = itemView.findViewById(R.id.studentHealth);
            StudentTestTime = itemView.findViewById(R.id.studentTestTime);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onClick(view, getAdapterPosition());
        }
    }
}
