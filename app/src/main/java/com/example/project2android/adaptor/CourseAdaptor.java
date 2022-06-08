package com.example.project2android.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2android.R;
import com.example.project2android.object.Course;

import java.util.ArrayList;

public class CourseAdaptor extends RecyclerView.Adapter<CourseAdaptor.MyViewHolder> {
    Context context;
    ArrayList<Course> courses;
    RecyclerViewClickListener listener;
    boolean isInstructor;

    public CourseAdaptor(Context context, ArrayList<Course> courses, RecyclerViewClickListener listener, boolean isInstructor) {
        this.context = context;
        this.courses = courses;
        this.listener = listener;
        this.isInstructor = isInstructor;
    }

    @NonNull
    @Override
    public CourseAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false);
        return new MyViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdaptor.MyViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.CourseName.setText(course.getName());
        holder.CourseTime.setText(course.getTime());
        holder.CourseDescription.setText(course.getDescription());
        holder.CourseType.setText(course.getType());
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView CourseName, CourseTime, CourseDescription, CourseType;
        RecyclerViewClickListener onNoteListener;

        public MyViewHolder(@NonNull View itemView, RecyclerViewClickListener onNoteListener) {
            super(itemView);
            CourseName = itemView.findViewById(R.id.CourseName);
            CourseTime = itemView.findViewById((R.id.CourseTime));
            CourseDescription = itemView.findViewById(R.id.CourseDescription);
            CourseType = itemView.findViewById(R.id.CourseType);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (isInstructor == false) {
                return;
            } else {
                onNoteListener.onClick(view, getAdapterPosition());
            }
        }
    }
}
