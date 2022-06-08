package com.example.project2android;

import android.app.Notification;
import android.app.NotificationManager;
import android.util.Log;
//import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.project2android.login.globalAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessengerService extends FirebaseMessagingService {

//    private static final String TAG = "hi";
    NotificationManager mNotificationManager;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("HIHI", "MESSAGE RECEIVED");
        System.out.println(remoteMessage.getData());
        Map<String, Object> docData = new HashMap<>();
        docData.put("body", remoteMessage.getData().get("body"));
        docData.put("title", remoteMessage.getData().get("title"));
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(date);
        docData.put("date", strDate);
        String uid = globalAuth.getUid();
        globalDB.getDb().collection("users").document(uid).collection("notifications").add(docData);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notifChannel")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Covider Notification: " + remoteMessage.getData().get("title"))
                .setContentText(String.valueOf(remoteMessage.getData().get("body")))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(0, notification);

//        super.onMessageReceived(remoteMessage);
//        Log.d(TAG, String.valueOf(remoteMessage.getNotification()));


//// playing audio and vibration when user se request
////        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
////        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
////        r.play();
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
////            r.setLooping(false);
////        }
//
////        // vibration
////        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
////        long[] pattern = {100, 300, 300, 300};
////        v.vibrate(pattern, -1);
//
//
//        int resourceImage = getResources().getIdentifier(remoteMessage.getNotification().getIcon(), "drawable", getPackageName());
//
//
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            builder.setSmallIcon(R.drawable.icontrans);
//            builder.setSmallIcon(R.drawable.icon);
//        } else {
////            builder.setSmallIcon(R.drawable.icon_kritikar);
//            builder.setSmallIcon(R.drawable.icon);
//        }
//
//
//
//        Intent resultIntent = new Intent(this, ChangeType.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        builder.setContentTitle(remoteMessage.getNotification().getTitle());
//        builder.setContentText(remoteMessage.getNotification().getBody());
//        builder.setContentIntent(pendingIntent);
//        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
//        builder.setAutoCancel(true);
//        builder.setPriority(Notification.PRIORITY_MAX);
//
//        mNotificationManager =
//                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//        {
//            String channelId = "Your_channel_id";
//            NotificationChannel channel = new NotificationChannel(
//                    channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_HIGH);
//            mNotificationManager.createNotificationChannel(channel);
//            builder.setChannelId(channelId);
//        }
//
//
//// notificationId is a unique int for each notification that you must define
//        mNotificationManager.notify(100, builder.build());


    }

}
