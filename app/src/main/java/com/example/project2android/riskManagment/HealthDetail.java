package com.example.project2android.riskManagment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2android.R;
import com.example.project2android.adaptor.UserHealthAdaptor;
import com.example.project2android.databinding.HealthDetailBinding;
import com.example.project2android.object.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/* Show Health Detail of Students */
public class HealthDetail extends Fragment {
    private HealthDetailBinding binding;
    private View view;
    private UserHealthAdaptor healthAdaptor;
    private Spinner filter;
    private ArrayList<User> students;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    ProgressBar pb;
    UserHealthAdaptor.RecyclerClickListener listener;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = HealthDetailBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        setOnClickListner();        // set click user detail
        setUserHealthAdaptor();     // set user health adaptor setting
        setStudentInformation();    // set student health information
        setHealthFilter();          // set initial filter for health by time
        return view;
    }

    private void setUserHealthAdaptor() {
        recyclerView = view.findViewById(R.id.student_list);
        pb = view.findViewById(R.id.idProgressBar);
        db = FirebaseFirestore.getInstance();

        students = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        healthAdaptor = new UserHealthAdaptor(view.getContext(), students, listener);
        recyclerView.setAdapter(healthAdaptor);
    }

    private void setHealthFilter() {
        filter = (Spinner) view.findViewById(R.id.dropdown_filter);
        String[] filter_item = {"daily", "weekly", "monthly"};
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(view.getContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, filter_item);
        filterAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        filter.setAdapter(filterAdapter);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        setStudentHealthInformation(0);
                        break;  // set student health information to daily
                    case 1:
                        setStudentHealthInformation(1);
                        break;  // set student health information to weekly
                    case 2:
                        setStudentHealthInformation(2);
                        break;  // set student health information to monthly
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                setStudentHealthInformation(0); // set student health information
            }
        });
    }

    private void setStudentInformation() {
        if (getArguments() != null) {
            pb.setVisibility(View.GONE);
            students.addAll(getArguments().getParcelableArrayList("students"));
        }
        healthAdaptor.notifyDataSetChanged();
    }

    private void setStudentHealthInformation(int time) {
        TextView StudentRisk, StudentPositive, TotalStudentRisk, TotalStudentPositive, RiskLevel;
        StudentRisk = view.findViewById(R.id.StudentRisk);
        StudentPositive = view.findViewById(R.id.StudentPositive);
        TotalStudentRisk = view.findViewById(R.id.TotalStudentRisk);
        TotalStudentPositive = view.findViewById(R.id.TotalStudentPositive);

        int riskStudent = 0;
        int positiveStudent = 0;

        for (User s : students) {
            if (s.getHealthByTime(time) == 1) {
                riskStudent++;
            } else if (s.getHealthByTime(time) == 2) {
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
    }

    private void setOnClickListner() {
        listener = new UserHealthAdaptor.RecyclerClickListener() {
            @Override
            public void onClick(View v, int position) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", students.get(position));
                Fragment newFragment = new UserDetail();
                newFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment, newFragment);
                fragmentTransaction.commit();
            }
        };
    }
}
