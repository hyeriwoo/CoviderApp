
package com.example.project2android;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.project2android.login.globalAuth;
import com.example.project2android.object.Building;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class globalDB {
    private static final String TAG = "Global Database";

    private static OnCompleteListener onCompleteListener;

    private static FirebaseFirestore db;
    public static FirebaseFirestore getDb(){
        return db;
    }
    public static void setDb(){
        globalDB.db = FirebaseFirestore.getInstance();
    }

    public static void setDb(FirebaseFirestore db) {
        globalDB.db = db;
    }
    public static void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        globalDB.onCompleteListener = onCompleteListener;
    }

    /*
    DB calls from DashboardFragment
     */
    public static void getBuildings() {
        db.collection("buildings").get().addOnCompleteListener(onCompleteListener);
    }

    public static void getCourses() {
        db.collection("courses").get().addOnCompleteListener(onCompleteListener);
    }

    /*
    DB calls from Building
     */
    public static void addVisitorTestResultData(String building_id,
                                                String user_id,
                                                HashMap<String, Object> data) {

        db.collection("buildings").document(building_id)
                .collection("visitors")
                .document(user_id)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Successfully added user as visitor");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Failed to add user as visitor", e);
                    }
                });
    }

    /*
    DB calls from User
     */
    public static void updateUserVisited(String user_id, String building_name,
                                         HashMap<String, Object> data) {
        db.collection("users").document(user_id)
                .collection("visited")
                .document(building_name)
                .set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Successfully updated user's visited building count");
                        } else {
                            Log.w(TAG, "Failed to update user's visited buildings");
                        }
                    }
                });
    }

    public static void getUserVisitedHistory(String user_id, HashMap<Building, Integer> data) {
        db.collection("users").document(user_id)
                .collection("visited")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<DocumentSnapshot> buildings =
                                    (ArrayList<DocumentSnapshot>)task.getResult().getDocuments();

                            for(DocumentSnapshot ds : buildings) {
                                String name = ds.getId();

                                for(Building building : data.keySet()) {
                                    if (building.getName().equals(name)) {
                                        data.put(building,
                                                Math.toIntExact((Long)ds.get("Num Visited")));
                                    }
                                }
                            }
                        }
                    }
                });
    }

    public static void addUserSurveyAnswer(String building_id,
                                           HashMap<String, ArrayList<Boolean>> data) {
        db.collection("buildings").document(building_id)
                .collection("surveyAnswers")
                .add(data);
    }

    public static void subscribeUserToBuildingTopic(String building_name) {
        FirebaseMessaging.getInstance().subscribeToTopic(building_name);
    }

    public static void clearAllVisitors(String building_id) {
        CollectionReference visitorsCol = db.collection("buildings")
                .document(building_id).collection("visitors");

        visitorsCol.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<DocumentSnapshot> allVisitors =
                            (ArrayList<DocumentSnapshot>)task.getResult().getDocuments();

                    for(DocumentSnapshot ds : allVisitors) {
                        visitorsCol.document(ds.getId()).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Visitor successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting visitor", e);
                                    }
                                });
                    }
                }
            }
        });
    }
}