package com.example.project2android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2android.object.Building;
import com.example.project2android.object.User;

import java.util.ArrayList;

public class BuildingListAdapter extends RecyclerView.Adapter<BuildingViewHolder> {

    private ArrayList<Building> buildings;
    private User user;
    private ArrayList<Building> buildingsInSchedule;
    private ArrayList<Building> filteredBuildings;
    private DashboardListFragment dashboardListFragment;

    private Context context;

    public BuildingListAdapter(ArrayList<Building> buildings,
                               User user,
                               ArrayList<Building> buildingsInSchedule,
                               ArrayList<Building> filteredBuildings,
                               Context context,
                               DashboardListFragment dashboardListFragment) {
        this.buildings = buildings;
        this.user = user;
        this.buildingsInSchedule = buildingsInSchedule;
        this.filteredBuildings = filteredBuildings;
        this.context = context;
        this.dashboardListFragment = dashboardListFragment;

    }

    @NonNull
    @Override
    public BuildingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context
                = parent.getContext();
        LayoutInflater inflater
                = LayoutInflater.from(context);

        // Inflate the layout
        View photoView = inflater.inflate(R.layout.building_list_layout,
                parent, false);

        BuildingViewHolder viewHolder
                = new BuildingViewHolder(photoView, dashboardListFragment, buildings, buildingsInSchedule, user);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingViewHolder viewHolder, int position) {
        final int index = viewHolder.getAdapterPosition();
        Building building = filteredBuildings.get(index);

        String hoursText = building.getHoursOfOperation().get(0);
        if (!hoursText.equals("CLOSED")) {
            hoursText = "OPEN: " + hoursText;
        }

        if (buildingsInSchedule.indexOf(building) != -1) {
            viewHolder.getBuildingHeader().setBackgroundColor(context.getResources().getColor(R.color.green, null));
        } else if (user.isFrequentlyVisited(building)) {
            viewHolder.getBuildingHeader().setBackgroundColor(context.getResources().getColor(R.color.teal_200, null));
        }

        viewHolder.getBuildingHeader().setText(building.getName());
        viewHolder.getRiskLevel().setText("Risk Level: " + building.getRiskLevel());
        viewHolder.getHourOfOperations().setText(hoursText);
    }

    @Override
    public int getItemCount() {
        return filteredBuildings.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}