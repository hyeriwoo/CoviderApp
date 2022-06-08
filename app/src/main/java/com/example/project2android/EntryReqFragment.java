package com.example.project2android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.project2android.databinding.FragmentEntryreqBinding;
import com.example.project2android.object.Building;
import com.example.project2android.object.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EntryReqFragment extends Fragment {

    private FragmentEntryreqBinding binding;

    private Building building;
    private ArrayList<Building> buildings;
    private ArrayList<Building> buildingsInSchedule;
    private ArrayList<String> entryReqs;
    private ArrayList<String> fulfills;
    private ArrayList<String> surveys;

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            // initialize
            entryReqs = new ArrayList<String>();
            fulfills = new ArrayList<String>();

            // collect data from dashboard fragment
            this.buildings = getArguments().getParcelableArrayList("buildings");
            String buildingName = getArguments().getString("buildingName");
            for(Building b : buildings) {
                if (b.getName().equals(buildingName)) {
                    this.building = b;
                    break;
                }
            }
            HashMap<String, String> entryReq = building.getEntryReq();
            for(String key : entryReq.keySet()) {
                entryReqs.add(key);
                fulfills.add(entryReq.get(key));
            }
            this.surveys = building.getSurveyQuestions();

            this.buildingsInSchedule = getArguments().getParcelableArrayList("buildingsInSchedule");
            this.user = getArguments().getParcelable("user");
        }

        binding = FragmentEntryreqBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout entryReq_ll = view.findViewById(R.id.entryReq_ll);

        RecyclerView recyclerView = new RecyclerView(getActivity());
        EntryReqAdapter entryReqAdapter =
                new EntryReqAdapter(entryReqs, fulfills,
                        getActivity(), this);

        recyclerView.setAdapter(entryReqAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        entryReq_ll.addView(recyclerView);

        LinearLayout survey_ll = view.findViewById(R.id.survey_ll);

        recyclerView = new RecyclerView(getActivity());
        SurveyAdapter surveyAdapter = new SurveyAdapter(surveys, getActivity());

        recyclerView.setAdapter(surveyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        survey_ll.addView(recyclerView);

        Button checkInButton = (Button)view.findViewById(R.id.checkInButton);
        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (surveyAdapter.surveyAnswered()) {
                    building.addSurveyAnswers(surveyAdapter.getSurveyAnswers());
                }

                if (building.checkIn(user)) {
                    toDashboard();
                } else {
                    // display to user that they cannot check in
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Cannot check in!");
                    alertDialogBuilder.setMessage("You have recently had symptoms or tested positive" +
                            " for COVID. Please keep the community safe by quarantining.");
                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            toDashboard();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // function to pass data back to dashboard fragment
    private void toDashboard() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("buildings", buildings);
        bundle.putParcelable("user", user);
        bundle.putParcelableArrayList("buildingsInSchedule", buildingsInSchedule);

        DashboardFragment dashboardFragment = new DashboardFragment();
        dashboardFragment.setArguments(bundle);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, dashboardFragment);
        fragmentTransaction.commit();
    }
}