package com.example.project2android;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2android.object.Building;
import com.example.project2android.object.User;

import java.util.ArrayList;

public class BuildingViewHolder extends RecyclerView.ViewHolder {

    private TextView buildingNameHeader;
    private TextView riskLevel;
    private TextView hourOfOperations;

    public BuildingViewHolder(@NonNull View itemView, DashboardListFragment dashboardFragment,
                              ArrayList<Building> buildings, ArrayList<Building> buildingsInSchedule,
                              User user) {
        super(itemView);

        buildingNameHeader = (TextView) itemView.findViewById(R.id.building_name_header);
        riskLevel = (TextView) itemView.findViewById(R.id.hidden_risk_level);
        hourOfOperations = (TextView) itemView.findViewById(R.id.hidden_hour_of_operations);

        View view = itemView;

        CardView cardView = (CardView) itemView.findViewById(R.id.base_building_card);
        LinearLayout hiddenView = (LinearLayout) itemView.findViewById(R.id.hidden_building_view);

        ImageButton expandCollapse = (ImageButton) itemView.findViewById(R.id.expand_collapse);
        expandCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hiddenView.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(cardView,
                            new AutoTransition());
                    hiddenView.setVisibility(View.GONE);
                    expandCollapse.setImageResource(R.drawable.baseline_expand_more_24);
                } else {
                    TransitionManager.beginDelayedTransition(cardView,
                            new AutoTransition());
                    hiddenView.setVisibility(View.VISIBLE);
                    expandCollapse.setImageResource(R.drawable.baseline_expand_less_24);
                }
            }
        });

        Button proceedButton = (Button) itemView.findViewById(R.id.proceed_button);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("buildings", buildings);
                bundle.putString("buildingName", (String) buildingNameHeader.getText());
                bundle.putParcelableArrayList("buildingsInSchedule", buildingsInSchedule);
                bundle.putParcelable("user", user);

                EntryReqFragment entryReqFragment = new EntryReqFragment();
                entryReqFragment.setArguments(bundle);

                dashboardFragment.loadFragment(entryReqFragment);
            }
        });
    }

    /*
    GETTERS
     */

    public TextView getBuildingHeader() {
        return this.buildingNameHeader;
    }

    public TextView getRiskLevel() {
        return this.riskLevel;
    }

    public TextView getHourOfOperations() {
        return this.hourOfOperations;
    }

}