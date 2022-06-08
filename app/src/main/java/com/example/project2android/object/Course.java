package com.example.project2android.object;

import java.util.*;

public class Course {
    // private static int course_id;
    private String name;
    private ArrayList<String> students = new ArrayList<>();
    private ArrayList<Long> dayOfWeek = new ArrayList<>();
    private long timeStart;
    private long timeEnd;
    private long type;
    private String building;
    private String instructor;
    private String description;

    public Course() {}

    public Course(String name, ArrayList<String> students, ArrayList<Long> dayOfWeek, long timeStart, long timeEnd, long type, String building, String instructor, String description) {
        this.name = name;
        this.students = students;
        this.dayOfWeek = dayOfWeek;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.type = type;
        this.building = building;
        this.instructor = instructor;
        this.description = description;
    }

    // getter
    // public static int getID() {return course_id;}
    public String getName() {return name;}
    public ArrayList<String> getStudents() {return students;}
    public String getType() {
        String result = "In-Person";
        if(type == 0) {result = "Online";}
        else if(type == 1) {result = "Hybrid";}
        else if(type == 2) {result = "In-Person";}
        return result;
    }

    private String timeConvertor(long time) {
        int min = ((int)time)%100;
        int hour = ((int)time)/100;
        if(hour > 12) {
            hour = hour - 12;
        }
        String minS = Integer.toString(min);
        String hourS = Integer.toString(hour);
        if(minS.equals("0")) {
            minS = "00";
        }
        return hourS + ":" + minS;
    }

    public String getTime() {
        String week = "";
        for(long w : dayOfWeek) {
            if(w == 0) { week+="M"; }
            else if(w == 1) { week+="T"; }
            else if(w == 2) { week+="W"; }
            else if(w == 3) { week+="TH"; }
            else if(w == 4) { week+="F"; }
        }
        week = week + " " + timeConvertor(timeStart) + " - " + timeConvertor(timeEnd);
        return week;
    }

    public String getDescription() {
        return description;
    }

    //public Building getBuilding() {return this.building;}
    //public Instructor getInstructor() {return this.instructor;}
    public String getBuilding() {return this.building;}
    public String getInstructor() {return this.instructor;}


    public void updateType(int type) {
        if(this.type != type) {
            notifyStudents();
        }
        this.type = type;
    }

    public void notifyStudents() {

    }

}
