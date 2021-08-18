package com.zebra.jamesswinton.geofencemonitorpoc;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.zebra.jamesswinton.geofencemonitorpoc.data.CustomGeofence;
import com.zebra.jamesswinton.geofencemonitorpoc.utils.GeofenceErrorMessages;
import com.zebra.jamesswinton.geofencemonitorpoc.utils.NotificationHelper;
import com.zebra.jamesswinton.geofencemonitorpoc.utils.StringHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorGeofenceService extends Service {

  // Consts.
  public static final String LaunchIntentAction = "start-monitoring";
  public static final String GeofenceListExtraKey = "goefence-list";

  // Geofencing
  private GeofencingClient mGeofencingClient;

  // Geofences
  private final Map<CustomGeofence, Geofence> mGeofenceList = new HashMap<>();

  // Trigger Pending Intent
  private PendingIntent mGeofenceTriggerPendingIntent;

  @Override
  public void onCreate() {
    super.onCreate();
    // Register Receiver
    registerReceiver(this.GeofenceTriggerReceiver, new IntentFilter("geofence-transition"));

    // Start Service
    startForeground(NotificationHelper.NOTIFICATION_ID,
        NotificationHelper.createNotification(this));
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null) {
      String intentAction = intent.getAction();
      if (intentAction == null) {
        return START_STICKY;
      } else if (intentAction.equals(LaunchIntentAction)) {
        initGeofenceMonitoring(intent);
        return START_STICKY;
      } else if (NotificationHelper.ACTION_STOP_SERVICE.equals(intentAction)) {
        stopSelf(startId);
        return START_NOT_STICKY;
      }
    }

    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(this.GeofenceTriggerReceiver);
    if (mGeofencingClient != null) {
      mGeofencingClient.removeGeofences(mGeofenceTriggerPendingIntent);
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Not supported");
  }

  @SuppressLint("MissingPermission")
  private void initGeofenceMonitoring(Intent intent) {
    if (!intent.hasExtra(GeofenceListExtraKey)) {
      Toast.makeText(this, "No geofences found", Toast.LENGTH_LONG).show();
      stopSelf();
      return;
    }

    // Get Client
    mGeofencingClient = LocationServices.getGeofencingClient(this);

    // Build Geofences List
    List<CustomGeofence> customGeofenceList = (ArrayList<CustomGeofence>)
        intent.getSerializableExtra(GeofenceListExtraKey);
    for (CustomGeofence customGeofence : customGeofenceList) {
      mGeofenceList.put(customGeofence, new Geofence.Builder()
          .setRequestId(customGeofence.getLabel())
          .setCircularRegion(customGeofence.getLat(), customGeofence.getLng(),
              customGeofence.getRadius())
          .setExpirationDuration(NEVER_EXPIRE)
          .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
          .build());
    }

    // Build Request from List
    GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        .addGeofences(new ArrayList<>(mGeofenceList.values()))
        .build();

    // Build Pending Intent to Broadcast Receiver (sends geofence triggers)
    Intent geofenceTriggerIntent = new Intent("geofence-transition");
    mGeofenceTriggerPendingIntent = PendingIntent.getBroadcast(this,
        0, geofenceTriggerIntent, PendingIntent.FLAG_CANCEL_CURRENT);

    // Add Geofences to monitor
    mGeofencingClient.addGeofences(geofencingRequest, mGeofenceTriggerPendingIntent)
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            Toast.makeText(this, "Geofence Monitoring Started", Toast.LENGTH_LONG).show();
          } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            stopSelf();
          }
        });
  }

  /**
   * Location Geofence Events
   */

  public BroadcastReceiver GeofenceTriggerReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context cx, Intent intent) {
      GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
      if (geofencingEvent.hasError()) {
        String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
        Log.e(this.getClass().getName(), errorMessage);
        return;
      }

      StringBuilder message = new StringBuilder();
      if (!geofencingEvent.hasError()) {
        List<Geofence> geofencingEvents = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofencingEvents) {
          for (CustomGeofence customGeofence : mGeofenceList.keySet()) {
            if (customGeofence.getLabel().equals(geofence.getRequestId())) {
              message.append(customGeofence.getDesc());
              message.append("\n\n\n");
            }
          }
        }
      }

      Intent launchDialogIntent = new Intent(MonitorGeofenceService.this, DialogActivity.class);
      launchDialogIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
      launchDialogIntent.putExtra("geofence-warning-title", "Caution!");
      launchDialogIntent.putExtra("geofence-warning-message", message.toString());
      launchDialogIntent.putExtra("geofence-transition-type",
          geofencingEvent.getGeofenceTransition());
      cx.startActivity(launchDialogIntent);

      if (geofencingEvent.getGeofenceTransition() == GEOFENCE_TRANSITION_ENTER){
        NotificationHelper.sendNotification(cx, "Caution!", message.toString());
      }
    }
  };

}