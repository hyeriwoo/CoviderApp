package com.example.project2android.object;

import java.util.ArrayList;
import java.util.HashSet;

public class Instructor extends User {
    private String user_id;
    private String name;
    private ArrayList<DailyCheck> record;
    private ArrayList<Course> schedule;
    private int isTestRequired;
    private HashSet<Building> visitedBuildings;

    public Instructor(String user_id, String name, String userEmail, ArrayList<String> courses) {
        super(user_id, name, userEmail, courses);
    }

    protected boolean requestNotify(Course course, int type) {
        return true;
    }

    protected void getClassInfo() {

    }
}
