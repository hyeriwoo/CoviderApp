package com.example.project2android.riskManagment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2android.R;
import com.example.project2android.adaptor.CourseAdaptor;
import com.example.project2android.adaptor.DailyCheckAdaptor;
import com.example.project2android.databinding.UserDetailBinding;
import com.example.project2android.object.Course;
import com.example.project2android.object.DailyCheck;
import com.example.project2android.object.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/* User Profile That Shows Name, Courses, DailyCheck */
public class UserDetail extends Fragment {
    private UserDetailBinding binding;
    private View view;
    private ArrayList<Course> courses;
    private ArrayList<DailyCheck> records;
    private User student;
    private FirebaseFirestore db;
    private RecyclerView courserv;
    private RecyclerView dailycheckrv;
    private CourseAdaptor courseAdaptor;
    private DailyCheckAdaptor dailyCheckAdaptor;
    private CourseAdaptor.RecyclerViewClickListener listener;
    private ProgressBar pb;
    private String user_id;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = UserDetailBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        setCourseAdaptor();
        setDailyCheckAdaptor();
        setStudentNameRecord();
        setStudentCourse();
        return view;
    }

    private void setCourseAdaptor() {
        courses = new ArrayList<>();
        courserv = view.findViewById(R.id.course_list);
        pb = view.findViewById(R.id.idProgressBar);
        courserv.setHasFixedSize(true);
        courserv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        courseAdaptor = new CourseAdaptor(view.getContext(), courses, listener, false);
        courserv.setAdapter(courseAdaptor);
    }

    private void setDailyCheckAdaptor() {
        records = new ArrayList<>();
        dailycheckrv = view.findViewById(R.id.dailycheck_list);
        dailycheckrv.setHasFixedSize(true);
        dailycheckrv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        dailyCheckAdaptor = new DailyCheckAdaptor(view.getContext(), records);
        dailycheckrv.setAdapter(dailyCheckAdaptor);
    }

    private void setStudentNameRecord() {
        TextView StudentName;
        StudentName = view.findViewById(R.id.StudentName);
        student = getArguments().getParcelable("user");
        records.addAll(student.getRecord());
        StudentName.setText(student.getName());
        dailyCheckAdaptor.notifyDataSetChanged();
    }

    private void setStudentCourse() {
        db = FirebaseFirestore.getInstance();
        for (String c : student.getSchedule()) {
            db.collection("courses").document(c).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot d) {
                    if (d.exists()) {
                        pb.setVisibility(View.GONE);
                        ArrayList<Long> dayOfWeek = (ArrayList<Long>) d.get("dayOfWeek");
                        Course c = new Course(d.getString("name"), null, dayOfWeek, d.getLong("timeStart"), d.getLong("timeEnd"),
                                d.getLong("type"), d.getString("building"), d.getString("instructor"), d.getString("description"));
                        courses.add(c);
                        courseAdaptor.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
