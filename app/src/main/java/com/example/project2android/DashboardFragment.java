package com.example.project2android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.project2android.databinding.FragmentDashboardBinding;
import com.example.project2android.manager.DashboardManager;
import com.example.project2android.manager.DashboardThread;
import com.example.project2android.object.Building;
import com.example.project2android.object.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DashboardFragment extends Fragment implements
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = "DashboardFragment";
    private FragmentDashboardBinding binding;
    private DashboardManager dashboardManager;
    private SupportMapFragment supportMapFragment;

    private User user;
    ArrayList<Integer> checkedIDs = new ArrayList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // get data of user and buildings
        try {
            ArrayList<Building> buildings = getArguments().getParcelableArrayList("buildings");
            this.user = getArguments().getParcelable("user");

            dashboardManager = new DashboardManager(user);
            if (getArguments() != null && buildings != null) {
                ArrayList<Bundle> buildingBundles = new ArrayList<>();
                ArrayList<ArrayList<String>> buildingHours = new ArrayList<>();
                ArrayList<Building> buildingsInSchedule = getArguments().getParcelableArrayList("buildingsInSchedule");
                ArrayList<LatLng> buildingLocations = new ArrayList<>();
                ArrayList<Building> buildingsFreqVisited = new ArrayList<>();

                for(Building b : buildings) {
                    buildingHours.add(b.getHoursOfOperation());
                    buildingLocations.add(b.getLocation());
                    if (user.isFrequentlyVisited(b)) {
                        buildingsFreqVisited.add(b);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("buildings", buildings);
                    bundle.putString("buildingName", b.getName());
                    bundle.putParcelable("user", user);
                    bundle.putParcelableArrayList("buildingsInSchedule", buildingsInSchedule);
                    buildingBundles.add(bundle);
                }

                dashboardManager.setData(buildings, buildingHours, buildingsInSchedule,
                        buildingsFreqVisited, buildingLocations, buildingBundles);
            } else {
                ExecutorService es = Executors.newFixedThreadPool(1);
                ReentrantLock lock = new ReentrantLock(true);

                DashboardThread dt1 =
                        new DashboardThread(dashboardManager, lock, (char)0);
                DashboardThread dt2 =
                        new DashboardThread(dashboardManager, lock, (char)1);

                es.submit(dt1);
                es.submit(dt2);

            }

        } catch (NullPointerException e) {
            System.out.println("Testing dashboard fragment");
        }

        // inflate layout
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View dashboardView = view;

        setupMap(dashboardView);
        setupList(dashboardView);

        // Filter application
        Button applyButton = (Button)view.findViewById(R.id.dashboard_apply);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChipGroup chipGroup = (ChipGroup)dashboardView.findViewById(R.id.chipGroup);
                checkedIDs = (ArrayList<Integer>) chipGroup.getCheckedChipIds();
                MaterialButton mapButton = (MaterialButton)dashboardView.findViewById(R.id.mapButton);
                MaterialButton listButton = (MaterialButton)dashboardView.findViewById(R.id.listButton);
                boolean mapChecked = mapButton.isChecked();
                boolean listChecked = listButton.isChecked();

                if (mapChecked) {
                    mapButton.performClick();
                } else if (listChecked) {
                    listButton.performClick();
                }

            }
        });
    }

    private void setupMap(final View view) {
        // Map dashboard selection
        MaterialButton mapButton = (MaterialButton)view.findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout frameLayout =
                        (FrameLayout)view.findViewById(R.id.dashboard_display);
                frameLayout.setContentDescription("Google Map Display");
                supportMapFragment = new SupportMapFragment();
                supportMapFragment.getMapAsync(DashboardFragment.this::onMapReady);
                loadFragment(supportMapFragment);
            }
        });
    }

    private void setupList(final View view) {
        // List dashboard selection
        MaterialButton listButton = (MaterialButton)view.findViewById(R.id.listButton);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout frameLayout =
                        (FrameLayout)view.findViewById(R.id.dashboard_display);
                frameLayout.setContentDescription("List Display");
                DashboardListFragment dashboardListFragment = new DashboardListFragment();
                dashboardListFragment.setBuildingData(dashboardManager.getBuildings(),
                        dashboardManager.getBundles(), dashboardManager.getBuildingsInSchedule(),
                        dashboardManager.getListFilteredBuildings(checkedIDs));
                loadFragment(dashboardListFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_display, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        ArrayList<Bundle> buildingBundles = dashboardManager.getBundles();
        ArrayList<LatLng> buildingLocations = dashboardManager.getBuildingLocations();

        Bundle bundle = buildingBundles.get(buildingLocations.indexOf(marker.getPosition()));
        EntryReqFragment entryReqFragment = new EntryReqFragment();
        entryReqFragment.setArguments(bundle);
        loadFragment(entryReqFragment);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // LatLang of USC map and center the map camera to center of campus
        LatLng southwest = new LatLng(34.01837844177507, -118.29155290706291);
        LatLng northeast = new LatLng(34.02518573233601, -118.27855667314748);
        LatLngBounds latLngBounds = new LatLngBounds(southwest, northeast);
        googleMap.setLatLngBoundsForCameraTarget(latLngBounds);
        googleMap.setMinZoomPreference(16);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(), 16));

        createMarkers(googleMap);

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {
                View v = View.inflate(getActivity(), R.layout.building_info_window, null);
                ArrayList<Building> buildings = dashboardManager.getBuildings();
                ArrayList<LatLng> buildingLocations = dashboardManager.getBuildingLocations();

                Building building = buildings.get(buildingLocations.indexOf(marker.getPosition()));

                TextView title = (TextView)v.findViewById(R.id.info_window_title);
                title.setText(building.getName());

                TextView hours = (TextView)v.findViewById(R.id.info_window_hours);
                String hoursText = building.getHoursOfOperation().get(0);
                if (!hoursText.equals("CLOSED")) {
                    hoursText = "OPEN: " + hoursText;
                }
                hours.setText(hoursText);

                TextView riskLevel = (TextView)v.findViewById(R.id.info_window_riskLevel);
                riskLevel.setText("Risk Level: " + building.getRiskLevel());

                return v;
            }

            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                ArrayList<LatLng> buildingLocations = dashboardManager.getBuildingLocations();
                ArrayList<Bundle> buildingBundles = dashboardManager.getBundles();

                Bundle bundle = buildingBundles.get(buildingLocations.indexOf(marker.getPosition()));
                EntryReqFragment entryReqFragment = new EntryReqFragment();
                entryReqFragment.setArguments(bundle);
                loadFragment(entryReqFragment);
            }
        });

        System.out.println("Finished creating map");
    }

    private void createMarkers(GoogleMap mMap) {
        Set<Building> resultSet = dashboardManager.getSetFilteredBuildings(checkedIDs);
        Set<Building> inScheduleSet = dashboardManager.getSetFilteredBuildings(R.id.filter_schedule);
        Set<Building> freqVisitedSet = dashboardManager.getSetFilteredBuildings(R.id.filter_freqVisited);

        for(Building b : resultSet) {
            String hourOfOperation = b.getHoursOfOperation().get(0);
            if (!hourOfOperation.equals("CLOSED")) {
                hourOfOperation = "OPEN: " + hourOfOperation;
            }

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(b.getLocation())
                    .title(b.getName())
                    .snippet("Risk Level: " + b.getRiskLevel() + "\n" + hourOfOperation));

            if (inScheduleSet.contains(b)) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if (freqVisitedSet.contains(b)) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            }
        }
    }
}