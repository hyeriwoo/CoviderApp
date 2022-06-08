package com.example.project2android.riskManagment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2android.FcmNotificationsSender;
import com.example.project2android.R;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.annotation.Nullable;

/* User Can Change Type and Update it to the database */
public class ChangeType extends AppCompatActivity {
    AlertDialog.Builder alertBuilder;
    String currentType = "";
    String courseName = "";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_type);
        getSupportActionBar().setTitle("Class Type");
        setCurrentType();           // set current type textview
        setInPersonButton();        // set in-person button and change type to in-person
        setHybridButton();          // set hybrid button and change type to hybrid
        setRemoteButton();          // set remote button and change type to remote
    }

    private void setCurrentType() {
        TextView CourseType = findViewById(R.id.CourseType);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentType = extras.getString("type");
            courseName = extras.getString("name");
            Log.e("tag", currentType);
        }
        CourseType.setText(currentType);
    }

    private void setInPersonButton() {
        Button b = (Button) findViewById(R.id.InPersonButton);
        if(currentType.equals("In-Person")) {
            b.setEnabled(false);
        }
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertChangeType("In-Person", 2);
            }
        });
    }

    private void setHybridButton() {
        Button b = (Button) findViewById(R.id.HybridButton);
        if(currentType.equals("Hybrid")) {
            b.setEnabled(false);
        }
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertChangeType("Hybrid", 1);
            }
        });
    }

    private void setRemoteButton() {
        Button b = (Button) findViewById(R.id.RemoteButton);
        if(currentType.equals("Remote")) {
            b.setEnabled(false);
        }
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertChangeType("Remote", 0);
            }
        });
    }

    private void alertChangeType(String changeType, long typeNumber) {
        db = FirebaseFirestore.getInstance();
        alertBuilder = new AlertDialog.Builder(this);
        AlertDialog.Builder successAlert = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Changing Class Type")
                .setMessage("Do you want to change class type from " + currentType + " to " + changeType + "?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("courses").document(courseName).update("type", typeNumber);
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/" + courseName, courseName,
                                courseName + " type has been changed to " + changeType, getApplicationContext(), ChangeType.this);
                        notificationsSender.SendNotifications();
                        successAlert.setTitle("Change Success")
                                .setMessage("Class type has been changed.")
                                .setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();
                    }
                })
                .show();
    }
}