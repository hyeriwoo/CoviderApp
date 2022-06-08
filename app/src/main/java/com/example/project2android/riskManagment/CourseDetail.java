package com.example.project2android.riskManagment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.project2android.R;
import com.example.project2android.databinding.CourseDetailBinding;
import com.example.project2android.object.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/* Course detail - brief health detail */
public class CourseDetail extends Fragment {
    private CourseDetailBinding binding;
    private View view;
    private TextView CourseName, CourseTime, CourseDescription, CourseType;
    private ArrayList<User> students;
    private FirebaseFirestore db;
    private ArrayList<String> studentID;
    ProgressBar pb;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = CourseDetailBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        setCourseInformation();         // set basic course information
        setBasic();                     // set basic information
        getStudentInformation();        // get student health information from firebase
        countDownTimer();
        setChangeTypeButton();          // set button to change type
        setHealthDetailButton();        // set button to health detail and send student info
        return view;
    }

    private void countDownTimer() {
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                pb.setVisibility(View.GONE);
                setStudentHealthInformation();  // set student health information
            }
        }.start();
    }

    private void setCourseInformation() {
        CourseName = view.findViewById(R.id.CourseName);
        CourseTime = view.findViewById((R.id.CourseTime));
        CourseDescription = view.findViewById(R.id.CourseDescription);
        CourseType = view.findViewById(R.id.CourseType);
        String name = "";
        String description = "";
        String time = "";
        String type = "";
        studentID = new ArrayList<>();
        if (getArguments() != null) {
            name = getArguments().getString("name");
            description = getArguments().getString("description");
            time = getArguments().getString("time");
            type = getArguments().getString("type");
            studentID = getArguments().getStringArrayList("student");
        }
        CourseName.setText(name);
        CourseTime.setText(time);
        CourseDescription.setText(description);
        CourseType.setText(type);
        // getSupportActionBar().setTitle(name);
    }

    private void setBasic() {
        pb = view.findViewById(R.id.idProgressBar);
        db = FirebaseFirestore.getInstance();
        students = new ArrayList<>();
    }

    private void getStudentInformation() {
        for (String s : studentID) {
            db.collection("users").document(s).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot d) {
                    if (d.exists()) {
                        User student = new User(s, d.getString("name"), d.getString("userEmail"), (ArrayList<String>) d.get("courses"));
                        student.setRecord();
                        students.add(student);
                    } else {
                        Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    protected void setStudentHealthInformation() {
        TextView StudentRisk, StudentPositive, TotalStudentRisk, TotalStudentPositive, RiskLevel;
        StudentRisk = view.findViewById(R.id.StudentRisk);
        StudentPositive = view.findViewById(R.id.StudentPositive);
        TotalStudentRisk = view.findViewById(R.id.TotalStudentRisk);
        TotalStudentPositive = view.findViewById(R.id.TotalStudentPositive);
        RiskLevel = view.findViewById(R.id.RiskLevel);

        int riskStudent = 0;
        int positiveStudent = 0;

        for (User s : students) {
            if (s.getHealthByTime(0) == 1) {
                riskStudent++;
            } else if (s.getHealthByTime(0) == 2) {
                positiveStudent++;
            }
        }

        int riskStudentProb = 0;
        int positiveStudentProb = 0;
        if (riskStudent != 0) {
            riskStudentProb = (int) ((100.00) * ((double) riskStudent / (double) students.size()));
        }
        if (positiveStudent != 0) {
            positiveStudentProb = (int) ((100.00) * ((double) positiveStudent / (double) students.size()));
        }

        StudentRisk.setText(Integer.toString(riskStudentProb) + "%");
        StudentPositive.setText(Integer.toString(positiveStudentProb) + "%");
        TotalStudentRisk.setText(Integer.toString(riskStudent) + "/" + Integer.toString(students.size()));
        TotalStudentPositive.setText(Integer.toString(positiveStudent) + "/" + Integer.toString(students.size()));

        if (riskStudentProb + positiveStudentProb >= 30) {
            RiskLevel.setText("High");
            RiskLevel.setTextColor(0xff990000);
        } else if (riskStudentProb + positiveStudentProb >= 20) {
            RiskLevel.setText("Moderate");
            RiskLevel.setTextColor(0xff990000);
        } else if (riskStudentProb + positiveStudentProb < 20) {
            RiskLevel.setText("Low");
            RiskLevel.setTextColor(0xff006600);
        }
    }

    private void setHealthDetailButton() {
        Button b = (Button) view.findViewById(R.id.healthDetailButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("students", students);
                Fragment newFragment = new HealthDetail();
                newFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment, newFragment);
                fragmentTransaction.commit();
            }
        });
    }

    private void setChangeTypeButton() {
        Button b = (Button) view.findViewById(R.id.changeTypeButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangeType.class);
                intent.putExtra("type", getArguments().getString("type"));
                intent.putExtra("name", getArguments().getString("name"));
                startActivity(intent);
            }
        });
    }

}
