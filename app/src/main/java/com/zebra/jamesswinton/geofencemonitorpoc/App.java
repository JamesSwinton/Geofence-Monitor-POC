package com.zebra.jamesswinton.geofencemonitorpoc;

import android.app.Application;
import com.zebra.jamesswinton.geofencemonitorpoc.data.CustomGeofence;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

  // Conf
  public static ArrayList<CustomGeofence> mGeofenceConfig;

  @Override
  public void onCreate() {
    super.onCreate();
  }
}
