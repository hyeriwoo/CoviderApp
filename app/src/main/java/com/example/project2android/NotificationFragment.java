package com.example.project2android;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project2android.adaptor.NotificationAdapter;
import com.example.project2android.databinding.FragmentNotificationBinding;
import com.example.project2android.login.globalAuth;
import com.example.project2android.object.Notification;
import com.example.project2android.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationFragment extends Fragment {
    private View view;
    private FragmentNotificationBinding binding;
    private User currentUser;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private ArrayList<Notification> notifications;
    ProgressBar pb;
    NotificationAdapter.RecyclerViewClickListener listener;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        pb = view.findViewById(R.id.idProgressBar);
        currentUser = getArguments().getParcelable("user");
        setNotificationAdaptor();   // setting recycler view
        getNotification();          // get notification info from firebase and update recycler view
        countDownTimer();
        return view;
    }

    private void setNotificationAdaptor() {
        notifications = new ArrayList<>();
        recyclerView = view.findViewById(R.id.notification_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        notificationAdapter = new NotificationAdapter(view.getContext(), notifications, listener, currentUser);
        recyclerView.setAdapter(notificationAdapter);
    }

    private void getNotification() {
        String uid = globalAuth.getUid();
        globalDB.getDb().collection("users")
                .document(uid).collection("notifications").orderBy("date", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String date =String.valueOf(document.get("date"));
                                Date date1 = null;
                                try {
                                    date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                notifications.add(new Notification(String.valueOf(document.getId()),
                                        String.valueOf(document.get("title")),
                                        String.valueOf(document.get("body")), date1));
                            }
                            pb.setVisibility(View.GONE);
                            notificationAdapter.notifyDataSetChanged();
                        } else {
                            System.out.println("Error getting documents: "+ task.getException());
                        }
                    }
                });
    }

    private void countDownTimer() {
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                checkNotification();
            }
        }.start();
    }

    private void checkNotification() {
        TextView check = view.findViewById(R.id.CheckNotification);
        if(notifications.isEmpty()) {
            check.setVisibility(View.VISIBLE);
        }
        else {
            check.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
