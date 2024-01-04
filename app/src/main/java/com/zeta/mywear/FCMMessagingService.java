package com.zeta.mywear;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FCMMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMMessagingService";
    public static final String NOTIFICATION_CHANNEL = "channel-notification";

    //@Override
    //public void onCreate() {
      //  super.onCreate();
      //  FirebaseApp.initializeApp(this);
    //}

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());




        handleMessage(remoteMessage);
    }

    private void handleMessage(RemoteMessage remoteMessage) {
        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }




    public void showNotification(String title, String messageBody) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Resources r = getResources();
        Notification notification = new Notification.Builder(this,NOTIFICATION_CHANNEL)
                .setTicker(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setContentIntent(pendingIntent).setDefaults(Notification.DEFAULT_ALL)
                .setChannelId(NOTIFICATION_CHANNEL)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(messageBody))
                .setAutoCancel(true).build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {

            /* Create or update. */
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, getString(R.string.app_name), importance);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(100, notification);
    }

    @Override
    public void onNewToken(String token) {
        // Handle the creation or refreshing of the FCM registration token.
        Log.d(TAG, "Refreshed token: " + token);

        // If you need to send the token to your server, implement that logic here.
    }

}