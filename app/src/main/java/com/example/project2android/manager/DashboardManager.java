package com.example.project2android.manager;

import android.os.Bundle;
import androidx.annotation.NonNull;

import com.example.project2android.R;
import com.example.project2android.globalDB;
import com.example.project2android.object.Building;
import com.example.project2android.object.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DashboardManager implements OnCompleteListener {

    private ArrayList<LatLng> buildingLocations;
    private ArrayList<Building> buildings;
    private ArrayList<ArrayList<String>> buildingHours;
    private ArrayList<Building> buildingsInSchedule;
    private ArrayList<Building> buildingsFreqVisited;
    private ArrayList<Bundle> buildingBundles;
    private User user;

    private boolean queryFinish;
    private char queryType;  // 0 = buildings, 1 = courses

    public DashboardManager(User user) {
        this.user = user;

        buildingLocations = new ArrayList<>();
        buildings = new ArrayList<>();
        buildingHours = new ArrayList<>();
        buildingsInSchedule = new ArrayList<>();
        buildingsFreqVisited = new ArrayList<>();
        buildingBundles = new ArrayList<>();
    }

    /*
    SETTERS
     */
    public void setQueryType(char queryType) { this.queryType = queryType; }

    public void setQueryFinish(boolean queryFinish) { this.queryFinish = queryFinish; }

    public void setData(ArrayList<Building> buildings, ArrayList<ArrayList<String>> buildingHours,
                        ArrayList<Building> buildingsInSchedule, ArrayList<Building> buildingsFreqVisited,
                        ArrayList<LatLng> buildingLocations, ArrayList<Bundle> buildingBundles) {
        this.buildingLocations = buildingLocations;
        this.buildings = buildings;
        this.buildingHours = buildingHours;
        this.buildingsInSchedule = buildingsInSchedule;
        this.buildingsFreqVisited = buildingsFreqVisited;
        this.buildingBundles = buildingBundles;
    }

    /*
    GETTERS
     */

    public boolean isQueryFinish() { return queryFinish; }

    public ArrayList<Building> getBuildings() {
        return this.buildings;
    }

    public ArrayList<LatLng> getBuildingLocations() {
        return this.buildingLocations;
    }

    public ArrayList<ArrayList<String>> getBuildingHours() {
        return this.buildingHours;
    }

    public ArrayList<Building> getBuildingsInSchedule() {
        return this.buildingsInSchedule;
    }

    public ArrayList<Building> getBuildingsFreqVisited() {
        return this.buildingsFreqVisited;
    }

    public ArrayList<Bundle> getBundles() {
        if (buildingBundles.isEmpty()) {
            Bundle bundle;
            for(Building building : buildings) {
                bundle = new Bundle();
                bundle.putParcelableArrayList("buildings", buildings);
                bundle.putString("buildingName", building.getName());
                bundle.putParcelable("user", user);
                bundle.putParcelableArrayList("buildingsInSchedule", buildingsInSchedule);

                buildingBundles.add(bundle);
            }
        }
        return buildingBundles;
    }

    /*
    METHODS
     */
    public Set<Building> getSetFilteredBuildings(ArrayList<Integer> checkedIDs) {
        Set<Building> result = new HashSet<>();

        if (checkedIDs.isEmpty() || checkedIDs.size() == 3) {
            result.addAll(buildings);
        } else {
            if (checkedIDs.contains(R.id.filter_regular)) {
                result.addAll(buildings);
                result.removeAll(buildingsInSchedule);
                result.removeAll(buildingsFreqVisited);
            }
            if (checkedIDs.contains(R.id.filter_freqVisited)) {
                result.addAll(buildingsFreqVisited);
            }
            if (checkedIDs.contains(R.id.filter_schedule)) {
                result.addAll(buildingsInSchedule);
            }
        }

        return result;
    }

    public Set<Building> getSetFilteredBuildings(int checkedID) {
        Set<Building> result = new HashSet<>();

        if (checkedID == R.id.filter_schedule) {
            result.addAll(buildingsInSchedule);
        } else if (checkedID == R.id.filter_freqVisited) {
            result.addAll(buildingsFreqVisited);
        }

        return result;
    }

    public ArrayList<Building> getListFilteredBuildings(ArrayList<Integer> checkedIDs) {
        ArrayList<Building> result = new ArrayList<>();

        if (checkedIDs.isEmpty() || checkedIDs.size() == 3) {
            result = (ArrayList<Building>) buildings.clone();
        } else {
            if (checkedIDs.contains(R.id.filter_regular)) {
                result = (ArrayList<Building>) buildings.clone();
                result.removeAll(buildingsInSchedule);
                result.removeAll(buildingsFreqVisited);
            }
            if (checkedIDs.contains(R.id.filter_freqVisited)) {
                System.out.println("When filtering: ");
                for(Building building : buildingsFreqVisited) {
                    System.out.println(building.getName());
                }
                result.addAll(buildingsFreqVisited);
            }
            if (checkedIDs.contains(R.id.filter_schedule)) {
                result.addAll(buildingsInSchedule);
            }
        }


        return result;
    }

    public void updateBuildings() {
        globalDB.setOnCompleteListener(this);
        globalDB.getBuildings();
    }

    public void updateCourses() {
        globalDB.setOnCompleteListener(this);
        globalDB.getCourses();
    }

    /*
    HELPER FUNCTIONS
     */
    @Override
    public void onComplete(@NonNull Task task) {
        QuerySnapshot qs = (QuerySnapshot) task.getResult();
        List<DocumentSnapshot> dsList = qs.getDocuments();

        if (queryType == 0) {
            for(DocumentSnapshot ds : dsList) {
                addBuilding(ds);
            }

        } else {
            ArrayList<String> courseIDs = user.getSchedule();
            Set<String> courseBuildings = new HashSet<>();
            int n = buildings.size();
            Building building;

            for(DocumentSnapshot ds : dsList) {
                if (courseIDs.contains(ds.getString("name"))) {
                    String courseBuilding = ds.getString("building");
                    courseBuildings.add(courseBuilding);
                }
            }

            for(int i = 0; i < n; i++) {
                building = buildings.get(i);

                if (courseBuildings.contains(building.getName())) {
                    buildingsInSchedule.add(building);
                }
                if (user.isFrequentlyVisited(building)) {
                    buildingsFreqVisited.add(building);
                }

            }
        }

        user.setVisitedHistory(buildings);

        queryFinish = true;
    }

    private void addBuilding(DocumentSnapshot ds) {
        String name = (String)ds.get("name");
        int riskLevel = Math.toIntExact((Long)ds.get("riskLevel"));
        int riskThreshold = Math.toIntExact((Long)ds.get("riskThreshold"));
        ArrayList<String> surveyQ = (ArrayList<String>)ds.get("surveyQuestions");
        HashMap<String, String> entryReqs = (HashMap<String, String>)ds.get("entryReq");
        ArrayList<String> hoursOfOperation = (ArrayList<String>)ds.get("hoursOfOperation");
        double lat = ds.getDouble("lat");
        double lng = ds.getDouble("lng");

        // create the building object
        Building building = new Building(ds.getId(), name, riskLevel, riskThreshold,
                surveyQ, entryReqs, hoursOfOperation, lat, lng);
        buildings.add(building);

        buildingLocations.add(new LatLng(lat, lng));
        buildingHours.add(hoursOfOperation);
    }
}