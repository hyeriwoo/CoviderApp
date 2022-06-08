package com.example.project2android;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.project2android.object.Building;

import java.util.ArrayList;

public class DashboardListFragment extends Fragment {

    private ArrayList<Building> buildings;
    private ArrayList<Bundle> buildingBundles;
    private ArrayList<Building> buildingsInSchedule;
    private ArrayList<Building> filteredBuildings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.buildingRecycler);
        BuildingListAdapter adapter = new BuildingListAdapter(
                buildings,
                buildingBundles.get(0).getParcelable("user"),
                buildingsInSchedule,
                filteredBuildings,
                getActivity(),
                this
        );

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void setBuildingData(ArrayList<Building> buildings,
                                ArrayList<Bundle> buildingBundles,
                                ArrayList<Building> buildingsInSchedule,
                                ArrayList<Building> filteredBuildings) {
        this.buildings = buildings;
        this.buildingBundles = buildingBundles;
        this.buildingsInSchedule = buildingsInSchedule;
        this.filteredBuildings = filteredBuildings;
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }
}