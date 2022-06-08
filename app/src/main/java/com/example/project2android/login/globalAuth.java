package com.example.project2android.login;


import com.example.project2android.object.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class globalAuth {
    private static FirebaseAuth mAuth;
    private static User currentUser;

    public static FirebaseAuth getAuth() {
        return mAuth;
    }

    public static void setAuth() {
        globalAuth.mAuth = FirebaseAuth.getInstance();
    }

    public static boolean getCurrentUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return false;
        } else {
            return true;
        }
    }

    public static String getUid() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return "";
        } else {
            return user.getUid();
        }
    }
}
