package com.example.project2android;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class TestGlobalDB {
    private static String TAG = "Global Database";

    private FirebaseFirestore db;

    public FirebaseFirestore getDb(){
        return db;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    public void setVisitorTestResultData(String building_id,
                                         String user_id,
                                         HashMap<String, Object> data) {
        db.collection("buildings").document(building_id)
                .collection("visitors")
                .document(user_id)
                .set(data);
    }
}