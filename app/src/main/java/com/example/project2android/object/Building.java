package com.example.project2android.object;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.project2android.FcmNotificationsSender;
import com.example.project2android.globalDB;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Building implements Parcelable {
    private String TAG = "BuildingClass";

    private String building_id;
    private String name;
    private int riskLevel;
    private int riskThreshold;
    private ArrayList<String> surveyQuestions;
    private HashSet<Survey> surveyAnswers;
    private HashSet<User> visitor;
    private HashSet<User> positiveOrSymptoms;
    private HashMap<String, String> entry_req;
    private ArrayList<String> hoursOfOperation;
    private double lat, lng;

    public Building(String name) {
        this.name = "test";
    }

    public Building() {
        this.building_id = "test";
        this.name = "test";
        this.riskLevel = 0;
        this.riskThreshold = 25;
        this.surveyQuestions = new ArrayList<>();
        this.surveyAnswers = new HashSet<>();
        this.visitor = new HashSet<>();
        this.positiveOrSymptoms = new HashSet<>();
        this.entry_req = new HashMap<>();
        this.hoursOfOperation = new ArrayList<>();
        this.lat = 0.0;
        this.lng = 0.0;
    }

    public Building(String building_id,
                    String name, int riskLevel, int riskThreshold,
                    ArrayList<String> surveyQuestions,
                    HashMap<String, String> entry_req,
                    ArrayList<String> hoursOfOperation,
                    double lat, double lng) {
        this.building_id = building_id;
        this.name = name;
        this.riskLevel = riskLevel;
        if (riskLevel > riskThreshold) {
            this.riskThreshold = riskLevel;
        } else {
            this.riskThreshold = riskThreshold;
        }
        this.surveyQuestions = surveyQuestions;
        this.surveyAnswers = new HashSet<Survey>();
        this.visitor = new HashSet<User>();
        this.positiveOrSymptoms = new HashSet<User>();
        this.entry_req = entry_req;
        this.hoursOfOperation = hoursOfOperation;
        this.lat = lat;
        this.lng = lng;
    }

    /*
    GETTERS
    */
    public String getName() { return name; }

    public String getBuilding_id() {return building_id;}

    public String getRiskLevel() {
        if (riskLevel < 25) {
            return "Low";
        } else if (riskLevel >= 25 && riskLevel <= 75) {
            return "Medium";
        }

        return "High";
    }

    public LatLng getLocation() {
        return new LatLng(lat, lng);
    }

    public ArrayList<String> getHoursOfOperation() {
        return hoursOfOperation;
    }

    public HashMap<String, String> getEntryReq() {return entry_req;}

    public ArrayList<String> getSurveyQuestions() {return this.surveyQuestions;}

    /*
    METHODS
     */
    public void addSurveyAnswers(ArrayList<Boolean> answers) {
        Survey survey = new Survey(answers);
        surveyAnswers.add(survey);

        HashMap<String, ArrayList<Boolean>> data = new HashMap<String, ArrayList<Boolean>>();
        data.put("answers", answers);

        globalDB.addUserSurveyAnswer(building_id, data);
    }

    public void notifyCloseContacts(Context context, Activity activity, int healthStatus) {
        String man = "";
        String sym = "";
        if(healthStatus == 2) {
            man = "Mandatory";
            sym = "tested positive";
        }
        else if(healthStatus == 1) {
            man = "Recommended";
            sym = "has covid risk";
        }
        String id = building_id.toUpperCase();
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/" + building_id, id,
                man + " Covid Notification: " + "Someone " + sym + " in " + id, context, activity);
        notificationsSender.SendNotifications();
    }

    public boolean checkIn(User user) {
        String recentHealth = user.getRecentHealth();

        updateVisitor(user);
        user.checkInBuilding(this);
        if (!recentHealth.equals("Negative")) {
            positiveOrSymptoms.add(user);
            return false;
        }

        updateRisk();
        // otherwise, add the user as a visitor and let them check in
        Log.e("check in",building_id);
        FirebaseMessaging.getInstance().subscribeToTopic(building_id);
        return true;
    }

    /*
    HELPER FUNCTIONS
     */
    private void updateRisk()  {
        int oldRiskLevel = riskLevel;
        int oldRiskThreshold = riskThreshold;

        updateRiskThreshold();
        updateRiskLevel();

        if (oldRiskLevel != riskLevel || oldRiskThreshold != riskThreshold) {
            // erase previous visitors and survey answers
            visitor = new HashSet<User>();
            positiveOrSymptoms = new HashSet<User>();

            // clear all visitors in database
            globalDB.clearAllVisitors(building_id);
        }
    }

    private void updateRiskThreshold() {
        // for every risky visitor, we increase the risk threshold
        // if no risky visitor, then theoretically the building always has low risk
        // otherwise, this "risk" increases with more risky visitors
        int numOfRiskyVisitors = positiveOrSymptoms.size();
        while(numOfRiskyVisitors >= 1) {
            riskThreshold += 5;
            if (riskThreshold > 100) {
                riskThreshold = 100;
                break;
            }
            numOfRiskyVisitors--;
        }
    }

    private void updateRiskLevel() {
        // for every 25 safe visitors, increase risk level until threshold
        int numOfVisitors = visitor.size();
        while(numOfVisitors >= 25) {
            riskLevel++;
            if (riskLevel > riskThreshold) {
                riskLevel = riskThreshold;
                break;
            }
            numOfVisitors-= 25;
        }

        riskLevel -= getNumOfSurveyYes();
        if (riskLevel < 0) {
            riskLevel = 0;
        }
    }

    private int getNumOfSurveyYes() {
        // check if a survey has all the questions answered to "yes"
        ArrayList<Boolean> answer;
        int numOfAllYes = 0;
        boolean allYes;
        for(Survey s : surveyAnswers) {
            answer = s.getAnswers();
            allYes = true;
            int n = answer.size();

            for(int i = 0; i < n; i++) {
                if (answer.get(i) == false) {
                    allYes = false;
                    break;
                }
            }

            // if so, reduce risk level
            if (allYes) {
                numOfAllYes++;
            }
        }

        return numOfAllYes / 3;
    }

    private void updateVisitor(User user) {
        // add user as a visitor to the building
        visitor.add(user);

        // create mapping of testing result
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss");

        HashMap<String, Object> data = new HashMap<>();
        data.put("date", now.format(dateTimeFormatter));
        String recentTestResult = user.getRecentHealth();
        switch(recentTestResult) {
            case "Symptoms":
                data.put("isPositive", false);
                data.put("isSymptom", true);
                break;
            case "Positive":
                data.put("isPositive", true);
                data.put("isSymptom", false);
                break;
            default:
                data.put("isPositive", false);
                data.put("isSymptom", false);
        }

        // add visitor's test result data to the database
        globalDB.addVisitorTestResultData(building_id,
                user.getId(), data);
    }

    /*
    METHODS FOR PARCELABLE
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(building_id);
        parcel.writeString(name);
        parcel.writeInt(riskLevel);
        parcel.writeInt(riskThreshold);
        parcel.writeStringList(surveyQuestions);
        parcel.writeSerializable(entry_req);
        parcel.writeStringList(hoursOfOperation);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
    }

    protected Building(Parcel in) {
        building_id = in.readString();
        name = in.readString();
        riskLevel = in.readInt();
        riskThreshold = in.readInt();
        surveyQuestions = in.createStringArrayList();
        entry_req = (HashMap<String, String>)in.readSerializable();
        hoursOfOperation = in.createStringArrayList();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<Building> CREATOR = new Creator<Building>() {
        @Override
        public Building createFromParcel(Parcel in) {
            return new Building(in);
        }

        @Override
        public Building[] newArray(int size) {
            return new Building[size];
        }
    };
}