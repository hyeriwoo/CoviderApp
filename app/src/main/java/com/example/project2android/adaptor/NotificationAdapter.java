package com.example.project2android.adaptor;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2android.R;
import com.example.project2android.globalDB;
import com.example.project2android.login.globalAuth;
import com.example.project2android.object.DailyCheck;
import com.example.project2android.object.Notification;
import com.example.project2android.object.User;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    ArrayList<Notification> notifications;
    NotificationAdapter.RecyclerViewClickListener listener;
    User currentUser;

    public NotificationAdapter(Context context, ArrayList<Notification> notifications, NotificationAdapter.RecyclerViewClickListener listener, User currentUser) {
        this.context = context;
        this.notifications = notifications;
        this.listener = listener;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.NotificationItem.setText(notification.getItem());
        holder.NotificationTime.setText(notification.getTime());
        holder.NotificationTitle.setText(notification.getTitle() + ": ");
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView NotificationItem, NotificationTime, NotificationTitle;
        RecyclerViewClickListener onNoteListener;

        public ViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            NotificationItem = itemView.findViewById(R.id.NotificationItem);
            NotificationTime = itemView.findViewById(R.id.NotificationTime);
            NotificationTitle = itemView.findViewById(R.id.NotificationTitle);
            this.onNoteListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // onNoteListener.onClick(view, getAdapterPosition());
            removeAt(view, getAdapterPosition());
        }

        private void removeAt(View view, int position) {
            AlertDialog.Builder alertBuilder;
            alertBuilder = new AlertDialog.Builder(view.getContext());
            alertBuilder.setTitle("Delete Notification")
                    .setMessage("You checked this notification. Do you want to delete this notification?")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            currentUser.deleteNotification(position);
                            String deleteId = notifications.get(position).getId();
                            String uid = globalAuth.getUid();
                            globalDB.getDb().collection("users")
                                    .document(uid).collection("notifications").document(deleteId).delete();
                            notifications.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, notifications.size());
                        }
                    })
                    .show();
        }

    }
}
