package com.example.project2android.riskManagment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2android.R;
import com.example.project2android.adaptor.CourseAdaptor;
import com.example.project2android.adaptor.DailyCheckAdaptor;
import com.example.project2android.databinding.FragmentCourselistBinding;
import com.example.project2android.object.Course;
import com.example.project2android.object.DailyCheck;
import com.example.project2android.object.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/* Show Course List of Instructor User */
public class CourseListFragment extends Fragment {
    private ArrayList<DailyCheck> records;
    private RecyclerView dailycheckrv;
    private DailyCheckAdaptor dailyCheckAdaptor;
    private FragmentCourselistBinding binding;
    private View view;
    private User currentUser;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ArrayList<Course> courses;
    private CourseAdaptor courseAdaptor;
    private ArrayList<User> students;
    ProgressBar pb;
    CourseAdaptor.RecyclerViewClickListener listener;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentCourselistBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        getSetCurrentUserInformation();
        setOnClickListner();        // clicking course lead to its detail
        setCourseAdaptor(view);     // course adaptor setting
        setDailyCheckAdaptor();
        getCourseInformation();     // get course information
        getDailyRecord();
        return view;
    }

    private void setDailyCheckAdaptor() {
        records = new ArrayList<>();
        dailycheckrv = view.findViewById(R.id.dailycheck_list);
        dailycheckrv.setHasFixedSize(true);
        dailycheckrv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        dailyCheckAdaptor = new DailyCheckAdaptor(view.getContext(), records);
        dailycheckrv.setAdapter(dailyCheckAdaptor);
    }

    private void getDailyRecord() {
        records.addAll(currentUser.getRecord());
        dailyCheckAdaptor.notifyDataSetChanged();
    }

    private void getSetCurrentUserInformation() {
        if (getArguments() != null) {
            currentUser = getArguments().getParcelable("user");
        }
        TextView lectureName = view.findViewById(R.id.lecture_name);
        String name = currentUser.getName();
        String lastName = name.substring(name.indexOf(" "), name.length());
        lectureName.setText(lastName + "'s Lecture");
    }

    private void setCourseAdaptor(View view) {
        recyclerView = view.findViewById(R.id.courses_list);
        pb = view.findViewById(R.id.idProgressBar);
        db = FirebaseFirestore.getInstance();

        courses = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        courseAdaptor = new CourseAdaptor(view.getContext(), courses, listener, true);
        recyclerView.setAdapter(courseAdaptor);
    }

    private void setOnClickListner() {
        listener = new CourseAdaptor.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("name", courses.get(position).getName());
                bundle.putString("description", courses.get(position).getDescription());
                bundle.putString("time", courses.get(position).getTime());
                bundle.putString("type", courses.get(position).getType());
                bundle.putStringArrayList("student", courses.get(position).getStudents());
                Fragment newFragment = new CourseDetail();
                newFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment, newFragment);
                fragmentTransaction.commit();
            }
        };
    }

    private void getCourseInformation() {
        courses.clear();
        db.collection("courses").whereEqualTo("instructor", currentUser.getId()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            pb.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                ArrayList<String> students = (ArrayList<String>) d.get("students");
                                ArrayList<Long> dayOfWeek = (ArrayList<Long>) d.get("dayOfWeek");
                                Course c = new Course(d.getString("name"), students, dayOfWeek, d.getLong("timeStart"), d.getLong("timeEnd"),
                                        d.getLong("type"), d.getString("building"), d.getString("instructor"), d.getString("description"));
                                courses.add(c);
                            }
                            courseAdaptor.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}