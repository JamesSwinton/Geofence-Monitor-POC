package com.zebra.jamesswinton.geofencemonitorpoc.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.view.WindowManager;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.InboxStyle;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.zebra.jamesswinton.geofencemonitorpoc.MonitorGeofenceService;
import com.zebra.jamesswinton.geofencemonitorpoc.R;
import com.zebra.jamesswinton.geofencemonitorpoc.data.CustomGeofence;
import com.zebra.jamesswinton.geofencemonitorpoc.utils.CustomDialog.DialogType;
import java.util.List;

public class NotificationHelper {

    // Constants
    private static final String PersistentChannelID = "com.zebra.geofencemonitorpoc.PERSISTENT";
    private static final String GeofenceChannelID = "com.zebra.geofencemonitorpoc.GEOFENCE";
    private static final String PersistentNotificationChannel = "Foreground Service Persistent Notification Channel";
    private static final String GeofenceTriggerChannel = "Geofence Trigger Notification Channel";
    public static final int NOTIFICATION_ID = 1;

    // Actions
    public static final String ACTION_STOP_SERVICE = "com.zebra.geofencemonitorpoc.STOP";

    public static Notification createNotification(Context cx) {

        // Create Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PersistentChannelID,
                    PersistentNotificationChannel, android.app.NotificationManager.IMPORTANCE_NONE);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Set Channel
            android.app.NotificationManager manager = (android.app.NotificationManager)
                    cx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }

        // Build Notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(cx,
            PersistentChannelID);

        // Set Notification Options
        notificationBuilder.setContentTitle("Geofence Monitoring Active")
                .setSmallIcon(R.drawable.ic_logo)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOngoing(true);

        // Add Buttons
        Intent stopIntent = new Intent(cx, MonitorGeofenceService.class);
        stopIntent.setAction(ACTION_STOP_SERVICE);
        PendingIntent stopPendingIntent = PendingIntent.getService(cx,
                0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action stopServiceAction = new NotificationCompat.Action(
                R.drawable.ic_stop,
                "Stop Service",
                stopPendingIntent
        );

        notificationBuilder.addAction(stopServiceAction);

        // Build & Return Notification
        return notificationBuilder.build();
    }

    public static void sendNotification(Context cx, String title, String message) {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager = (NotificationManager)
            cx.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the channel for the notification
            NotificationChannel mChannel = new NotificationChannel(GeofenceChannelID, GeofenceTriggerChannel,
                    NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        /// Build Notification
        Notification geofenceTriggeredNotification =
            new NotificationCompat.Builder(cx, GeofenceChannelID)
                .setSmallIcon(R.drawable.ic_warning)
                .setChannelId(GeofenceChannelID)
                .setContentTitle(title)
                .setContentText(message)
                .build();

        // Issue the notification
        mNotificationManager.notify(0, geofenceTriggeredNotification);
    }

}
