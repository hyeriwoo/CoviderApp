package com.example.project2android.login;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.project2android.NotificationFragment;
import com.example.project2android.R;
import com.example.project2android.ReportActivity;
import com.example.project2android.globalDB;
import com.example.project2android.object.User;
import com.example.project2android.DashboardFragment;
import com.example.project2android.riskManagment.CourseListFragment;
import com.example.project2android.riskManagment.UserDetail;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class TransitionFromLogin extends AppCompatActivity {
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        getSupportActionBar().setTitle("Covider @USC");
        checkLogin();
        findViewById(R.id.navigationView).setVisibility(View.VISIBLE);
        getCurrentUserInformation();
        createNotificationChannel();
    }

    private void checkLogin() {
        if (!globalAuth.getCurrentUser()) {

            startActivity(new Intent(TransitionFromLogin.this, LoginActivity.class));
        }
    }

    private void createNotificationChannel() {
        System.out.println("NotificationChannel Created");
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notifChannel";
//            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notifChannel", name, importance);
//            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void getCurrentUserInformation() {
        FirebaseAuth mAuth = globalAuth.getAuth();
        FirebaseUser user = mAuth.getCurrentUser();
        globalDB.getDb().collection("users").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot d) {
                        if (d.exists()) {
                            currentUser = new User(user.getUid(), d.getString("name"), d.getString("userEmail"), (ArrayList<String>)(d.get("courses")));
                            currentUser.setRecord();
                            currentUser.setIsInstructor(d.getBoolean("isInstructor"));
                            currentUser.subscribeCourse();
                            replaceFragment(new DashboardFragment());
                            setNavigationBar();
                            setLogOutButton();
                            setReportButton();
                        } else {
                            Toast.makeText(TransitionFromLogin.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
            }
        };
    }

    private void setNavigationBar() {
        BottomNavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new DashboardFragment());
                    break;
                case R.id.notification:
                    replaceFragment(new NotificationFragment());
                    break;
                case R.id.classes:
                    if (currentUser.getIsInstructor() == true) {
                        replaceFragment(new CourseListFragment());
                    } else {
                        replaceFragment(new UserDetail());
                    }
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", currentUser);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }

    private void setLogOutButton() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        AlertDialog.Builder successAlert = new AlertDialog.Builder(this);
        Button b = (Button) findViewById(R.id.logoutBtn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalAuth.getCurrentUser()) {
                    alertBuilder.setTitle("Sign Out").setMessage("Do you really want to sign out?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(TransitionFromLogin.this, LoginActivity.class));
                                }
                            }).show();
                }
            }
        });
    }

    private void setReportButton() {
        FloatingActionButton b = (FloatingActionButton) findViewById(R.id.fab);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransitionFromLogin.this, ReportActivity.class);
                //To be implemeneted!!!!:
                intent.putExtra("user", currentUser);
                startActivity(intent);
            }
        });

    }

}
