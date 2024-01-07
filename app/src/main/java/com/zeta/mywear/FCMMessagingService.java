package com.zeta.mywear;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;

import java.util.Map;


public class FCMMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMMessagingService";
    public static final String NOTIFICATION_CHANNEL = "channel-notification";
    private static final String PREFERENCES_NAME = "MyPreferences";
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
        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage);
    }




    public void showNotification(String title, RemoteMessage remoteMessage) {
        String messageBody = remoteMessage.getNotification().getBody();
        Map<String, String> data = remoteMessage.getData();
        Intent intent = new Intent(this, MainActivity.class);
        if (!data.isEmpty()) {
            String id_intervento = data.get("id_intervento");
            String impianto = data.get("impianto");
            String tipo_intervento = data.get("tipo_intervento");
            String data_richiesta = data.get("data_richiesta");
            String stato = data.get("stato");
            String id_stato = data.get("id_stato");

            intent.putExtra("id_intervento", id_intervento);
            intent.putExtra("impianto", impianto);
            intent.putExtra("tipo_intervento", tipo_intervento);
            intent.putExtra("data_richiesta", data_richiesta);
            intent.putExtra("stato", stato);
            intent.putExtra("id_stato", id_stato);
        }


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
        try {
            String TokenLogin = getToken();
            ApiManager.sendToken(getBaseUrl(), TokenLogin, token, new ApiManager.ApiCallback() {
                @Override
                public void onResponse(ApiManager.ApiResponse response) throws JSONException {
                    if (response.getStatusCode() == 200) {

                    } else {
                        // Gestire il fallimento
                    }
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // If you need to send the token to your server, implement that logic here.
    }
    private String getToken() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString("token", null);
    }

    private String getBaseUrl() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString("linkAzienda", null);
    }
}
