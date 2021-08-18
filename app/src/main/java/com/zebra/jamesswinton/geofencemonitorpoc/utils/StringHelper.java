package com.zebra.jamesswinton.geofencemonitorpoc.utils;

import android.text.TextUtils;
import com.google.android.gms.location.Geofence;
import com.zebra.jamesswinton.geofencemonitorpoc.R;
import java.util.ArrayList;
import java.util.List;

public class StringHelper {
  public static String getGeofenceTransitionDetails(int geofenceTransition,
      List<Geofence> triggeringGeofences) {
    String geofenceTransitionString = getTransitionString(geofenceTransition);

    // Get the Ids of each geofence that was triggered.
    ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
    for (Geofence geofence : triggeringGeofences) {
      triggeringGeofencesIdsList.add(geofence.getRequestId());
    }
    String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

    return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
  }

  private static String getTransitionString(int transitionType) {
    switch (transitionType) {
      case Geofence.GEOFENCE_TRANSITION_ENTER:
        return "ENTERED";
      case Geofence.GEOFENCE_TRANSITION_EXIT:
        return "EXITED";
      default:
        return "UNKNOWN";
    }
  }

}
