package com.example.project2android.object;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


import com.example.project2android.FcmNotificationsSender;
import com.example.project2android.globalDB;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;



public class User implements Parcelable {
    private String user_id;
    private String name;
    private String userEmail;
    private ArrayList<DailyCheck> record = new ArrayList<>();
    private ArrayList<String> courses = new ArrayList<>();
    private boolean isInstructor;
    private ArrayList<Notification> notifications = new ArrayList<>();
    private HashMap<Building, Integer> visitedHistory = new HashMap<>();

    // empty user constructor for test
    public User() {
        this.user_id = "dude";
        this.name = "dude";
        this.userEmail = "dude@usc.edu";
        this.courses = new ArrayList<String>();
    }

    public User(String user_id, String name, String userEmail, ArrayList<String> courses) {
        this.user_id = user_id;
        this.name = name;
        this.userEmail = userEmail;
        this.courses = courses;
    }

//    // get notification from firebase REPLACE THIS!!!
//    public ArrayList<Notification> getNotification() {
////        Notification n = new Notification("test1", "march22");
////        notifications.add(n);
//
//        String uid = globalAuth.getUid();
//        globalDB.getDb().collection("users")
//                .document(uid).collection("notifications").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                String date =String.valueOf(document.get("date"));
//                                Date date1 = null;
//                                try {
//                                    date1 = new SimpleDateFormat("yyyy-mm-dd").parse(date);
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                                notifications.add(new Notification(String.valueOf(document.get("title"))
//                                        + ": " + String.valueOf(document.get("body")), date1));
//                                System.out.println(String.valueOf(document.get("body")) + String.valueOf(document.get("date")));
//                            }
//                        } else {
//                            System.out.println("Error getting documents: "+ task.getException());
//                        }
//                    }
//                });
//
//
//        return notifications;
//    }

//    public void deleteNotification(int position) {
//        notifications.remove(position);
//
//    }

    public void setRecord() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user_id).collection("record").orderBy("testDate", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        DailyCheck dc = new DailyCheck(d.getTimestamp("testDate").toDate(), d.getLong("healthStatus"));
                        record.add(dc);
                    }

                }
            }
        });
    }

    public void setRecordTest(ArrayList<DailyCheck> newRecord) {
        record = newRecord;
    }

    public void setIsInstructor(boolean isInstructor) {
        this.isInstructor = isInstructor;
    }

    public void setVisitedHistory(ArrayList<Building> buildings) {
        for(Building building : buildings) {
            visitedHistory.put(building, 0);
        }

        globalDB.getUserVisitedHistory(user_id, visitedHistory);
    }

    public boolean getIsInstructor() {
        return this.isInstructor;
    }

    // getter
    public String getId() {
        return this.user_id;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<DailyCheck> getRecord() {
        return this.record;
    }

    // return the most recent daily check
    private DailyCheck getRecentDailyCheck() {
        Comparator<DailyCheck> cmp = new Comparator<DailyCheck>() {
            @Override
            public int compare(DailyCheck dc1, DailyCheck dc2) {
                return dc1.getDate().compareTo(dc2.getDate());
            }
        };
        return Collections.max(record, cmp);
    }

    // return the health status of the most recent daily check
    public String getRecentHealth() {
        if (record == null) {
            return "No Test";
        }
        if (record.size() == 0) {
            return "No Test";
        }
        String health = "No Info";
        int test = getRecentDailyCheck().getHealthStatus();
        if (test == 0) {
            health = "Negative";
        } else if (test == 1) {
            health = "Symptoms";
        } else if (test == 2) {
            health = "Positive";
        } else if (test == 3) {
            health = "Invalid";
        }
        return health;
    }

    // return the test time of the most recent daily check
    public String getRecentTestTime() {
        if (record == null) {
            return "No Test Time Information";
        }
        if (record.size() == 0) {
            return "No Test Time Information";
        }
        Date test = getRecentDailyCheck().getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return dateFormat.format(test);
    }

    // compare whether date is within the given time range

    private int compareDate(int time, Date testTime) {
        int result = 0; // 0 = not within time range, 1 = within time range
        final ZoneOffset zoneOffset = ZoneOffset.UTC;
        LocalDateTime now = LocalDateTime.now();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        long testHour = TimeUnit.MILLISECONDS.toHours(testTime.toInstant().toEpochMilli());
        long nowHour = TimeUnit.MILLISECONDS.toHours(now.toInstant(zoneOffset).toEpochMilli());


        if (nowHour - testHour <= time) {
            result = 1;
        }
        return result;
    }

    // return health status within the given time range
    public int getHealthByTime(int time) {
        int result = 0;     // 0 = negative, 1 = at risk, 2 = positive, 3 = invalid
        int resultSize = 0;
        int invalidResult = 3;
        int invalidSize = 0;
        if (record == null) {
            return result;
        }

        if (time == 0) {         // daily
            for (DailyCheck r : record) {
                if (compareDate(24, r.getDate()) == 1) {
                    if (r.getHealthStatus() == 0) {
                        resultSize++;
                    }
                    if (r.getHealthStatus() == 3) {
                        invalidSize++;
                    }
                    if (r.getHealthStatus() == 1) {
                        result = 1;
                        invalidResult = 1;
                    } else if (r.getHealthStatus() == 2) {
                        result = 2;
                        invalidResult = 2;
                    }
                }
            }
        } else if (time == 1) {    // weekly
            for (DailyCheck r : record) {
                if (compareDate(168, r.getDate()) == 1) {
                    if (r.getHealthStatus() == 0) {
                        resultSize++;
                    }
                    if (r.getHealthStatus() == 3) {
                        invalidSize++;
                    }
                    if (r.getHealthStatus() == 1) {
                        result = 1;
                        invalidResult = 1;
                    } else if (r.getHealthStatus() == 2) {
                        result = 2;
                        invalidResult = 2;
                    }
                }
            }
        } else if (time == 2) {    // monthly
            for (DailyCheck r : record) {
                if (compareDate(720, r.getDate()) == 1) {
                    if (r.getHealthStatus() == 0) {
                        resultSize++;
                    }
                    if (r.getHealthStatus() == 3) {
                        invalidSize++;
                    }
                    if (r.getHealthStatus() == 1) {
                        result = 1;
                        invalidResult = 1;
                    } else if (r.getHealthStatus() == 2) {
                        result = 2;
                        invalidResult = 2;
                    }
                }
            }
        }

        if (resultSize != 0 && result == 0) {
            return result;
        } else if (invalidSize != 0 && invalidResult == 3) {
            return invalidResult;
        }

        return result;
    }

    public ArrayList<String> getSchedule() {
        return this.courses;
    }

    public void subscribeCourse() {
        if (courses == null) {
            return;
        }
        for (String c : courses) {
            Log.d("course", c);
            FirebaseMessaging.getInstance().subscribeToTopic(c);
        }
    }

    public void addRecord(DailyCheck newDailyCheck) {
        Log.e("add record", newDailyCheck.getStringHealthStatus());
        DailyCheck d = new DailyCheck(newDailyCheck.getDate(), newDailyCheck.getHealthStatus());
        Log.e("dd", d.getStringDate()+ " "+ d.getHealthStatus());
        record.add(d);
        for(DailyCheck r: record) {
            Log.e("recprd list", r.getStringDate());
        }
    }

    public void notifyBuilding(Context context, Activity activity, int healthStatus) {
        // notify all buildings that user checked in to
        for (Building b : visitedHistory.keySet()) {
            Log.e("sending", b.getName());
            b.notifyCloseContacts(context, activity, healthStatus);
        }
    }

    public void notifyClassStudent(Context context, Activity activity, int healthStatus) {
        // notify all class if student is positive
        String man = "";
        String sym = "";
        if(healthStatus == 2) {
            man = "Mandatory";
            sym = " tested positive";
        }
        else if(healthStatus == 1) {
            man = "Recommended";
            sym = " has covid risk";
        }
        for(String c : courses) {
            FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/" + c, c,
                    man + " Covid Notification: " + "One of class mates in " + c + sym, context, activity);
            notificationsSender.SendNotifications();
        }
    }

    public void notifyClassInstructor(Context context, Activity activity, int healthStatus) {
        // notify all class if instructor is positive, and update class type
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
        for(String c : courses) {
            FirebaseFirestore.getInstance().collection("courses").document(c).update("type", 0);
            FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/" + c, c,
                    man + " Covid Notification: " + "Instructor " + sym + ". " + c + " will be conducted online.", context, activity);
            notificationsSender.SendNotifications();
        }
    }

    protected void checkInBuilding(Building building) {
        int count;
        if (!visitedHistory.containsKey(building)) {
            count = 1;
        } else {
            count = visitedHistory.get(building) + 1;
        }
        visitedHistory.put(building, count);

        HashMap<String, Object> data = new HashMap<>();
        data.put("Num Visited", count);

        globalDB.updateUserVisited(user_id, building.getName(), data);
    }

    public boolean isFrequentlyVisited(Building visited) {
        if (!visitedHistory.containsKey(visited)) {
            return false;
        }

        return visitedHistory.get(visited) > 2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /* parcel stuff */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.user_id);
        parcel.writeString(this.name);
        parcel.writeString(this.userEmail);
        parcel.writeTypedList(this.record);
        parcel.writeBoolean(this.isInstructor);
        parcel.writeStringList(this.courses);
        parcel.writeInt(visitedHistory.size());
        for(HashMap.Entry<Building, Integer> entry : visitedHistory.entrySet()){
            parcel.writeParcelable(entry.getKey(), i);
            parcel.writeInt(entry.getValue());
        }
    }

    protected User(Parcel in) {
        this.user_id = in.readString();
        this.name = in.readString();
        this.userEmail = in.readString();
        this.record = in.createTypedArrayList(DailyCheck.CREATOR);
        this.isInstructor = in.readBoolean();
        in.readStringList(courses);
        int size = in.readInt();
        Map<Building, Integer> map = new HashMap<Building, Integer>(size);
        for(int i = 0; i < size; i++){
            map.put(in.readParcelable(Building.class.getClassLoader()), in.readInt());
        }
        this.visitedHistory = (HashMap<Building, Integer>) map;
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
