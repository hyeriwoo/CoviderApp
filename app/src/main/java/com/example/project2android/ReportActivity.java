package com.example.project2android;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2android.login.globalAuth;
import com.example.project2android.object.DailyCheck;
import com.example.project2android.object.User;
import com.example.project2android.riskManagment.ChangeType;
import com.google.firebase.firestore.FieldValue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        setTitle("Covider @USC");

        User user = getIntent().getParcelableExtra("user");
        RadioGroup symptomsRadioGroup = (RadioGroup) findViewById(R.id.symptomsGroup);
        RadioGroup resultsRadioGroup = (RadioGroup) findViewById(R.id.covid_result_radio_group);
        RadioGroup testRadioGroup = (RadioGroup) findViewById(R.id.covidTestRadio);
        testRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = testRadioGroup.findViewById(checkedId);
                int index = testRadioGroup.indexOfChild(radioButton);
                if (index == 0) {
                    findViewById(R.id.covid_result_group).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.covid_result_group).setVisibility(View.GONE);
                }
            }
        });


        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int healthStatus = 0;
                DailyCheck newRecord;
                int testRadioButtonId = testRadioGroup.getCheckedRadioButtonId();
                int resultsRadioButtonId = resultsRadioGroup.getCheckedRadioButtonId();
                int symptomsRadioButtonId = symptomsRadioGroup.getCheckedRadioButtonId();
                if (testRadioGroup.indexOfChild(testRadioGroup.findViewById(testRadioButtonId)) == 0 && resultsRadioButtonId == -1) {
                    Toast.makeText(ReportActivity.this, "Please answer all questions", Toast.LENGTH_SHORT).show();
                } else if (testRadioButtonId != -1 && symptomsRadioButtonId != -1) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("testDate", FieldValue.serverTimestamp());
                    data.put("healthStatus", 0);
                    if (symptomsRadioGroup.indexOfChild(symptomsRadioGroup.findViewById(symptomsRadioButtonId)) == 0) {
                        data.put("healthStatus", 1);
                        healthStatus = 1;
                    }
                    if (resultsRadioGroup.indexOfChild(resultsRadioGroup.findViewById(resultsRadioButtonId)) == 0) {
                        data.put("healthStatus", 2);
                        healthStatus = 2;
                    }
                    String uid = globalAuth.getUid();
                    globalDB.getDb().collection("users").document(uid).collection("record").add(data);
                    LocalDateTime ldt = LocalDateTime.now();
                    Instant instant = ldt.toInstant(ZoneOffset.UTC);
                    newRecord = new DailyCheck(Date.from(instant), healthStatus);
                    user.addRecord(newRecord);

                    if(healthStatus == 2) {
                        user.notifyBuilding(getApplicationContext(), ReportActivity.this, 2);
                        if(user.getIsInstructor() == true) {
                            user.notifyClassInstructor(getApplicationContext(), ReportActivity.this, 2);
                            alertChanges();
                        }
                        else {
                            user.notifyClassStudent(getApplicationContext(), ReportActivity.this, 2);
                            Toast.makeText(ReportActivity.this, "Health Reported Successfully Logged", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    else if(healthStatus == 1) {
                        user.notifyBuilding(getApplicationContext(), ReportActivity.this, 1);
                        if(user.getIsInstructor() == true) {
                            user.notifyClassInstructor(getApplicationContext(), ReportActivity.this, 1);
                            alertChanges();
                        }
                        else {
                            user.notifyClassStudent(getApplicationContext(), ReportActivity.this, 1);
                            Toast.makeText(ReportActivity.this, "Health Reported Successfully Logged", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    else {
                        Toast.makeText(ReportActivity.this, "Health Reported Successfully Logged", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(ReportActivity.this, "Please answer all questions", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        user.setRecord();
    }

    private void alertChanges() {
        AlertDialog.Builder alertBuilder;
        alertBuilder = new AlertDialog.Builder(this);
        AlertDialog.Builder successAlert = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Send Notification and Change Class Type")
                .setMessage("Your lectures have been changed on remote, and students have been notified.")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

}