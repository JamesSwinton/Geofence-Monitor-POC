package com.zebra.jamesswinton.geofencemonitorpoc;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.zebra.jamesswinton.geofencemonitorpoc.data.CustomGeofence;
import com.zebra.jamesswinton.geofencemonitorpoc.databinding.ActivitySplashScreenBinding;
import com.zebra.jamesswinton.geofencemonitorpoc.utils.CustomDialog;
import com.zebra.jamesswinton.geofencemonitorpoc.utils.CustomDialog.DialogType;
import com.zebra.jamesswinton.geofencemonitorpoc.utils.FileUtils.AssetFileUtils;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SplashScreenActivity extends AppCompatActivity {

  // UI
  private ActivitySplashScreenBinding mDataBinding;

  // File Names
  public static final String GeofenceConfigFileName = "geofence_config.json";

  // Configs
  private ArrayList<CustomGeofence> mGeofenceList = null;

  // Delay
  private final long mStartTime = System.currentTimeMillis();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);
    Glide.with(this)
        .load(R.raw.logo)
        .listener(new RequestListener<Drawable>() {
          @Override
          public boolean onLoadFailed(@Nullable GlideException e, Object model,
              Target<Drawable> target, boolean isFirstResource) {
            // Load Settings
            loadConfigFilesAsync();
            return false;
          }

          @Override
          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
              DataSource dataSource, boolean isFirstResource) {
            // Load Settings
            loadConfigFilesAsync();
            return false;
          }
        })
        .into(mDataBinding.gifView);
  }

  private void loadConfigFilesAsync() {
    new Thread(() -> {
      // Loop Files and Parse to POJOs
      try {
        File geofenceConfigFile = new File(getExternalFilesDir(null) + "/" + GeofenceConfigFileName);
        if (!geofenceConfigFile.exists()) {
          AssetFileUtils.CopyAssetToFile(SplashScreenActivity.this,
              geofenceConfigFile.getName(), geofenceConfigFile);
        }

        JsonReader reader = new JsonReader(new FileReader(geofenceConfigFile));
        mGeofenceList = new Gson().fromJson(reader,
            new TypeToken<List<CustomGeofence>>(){}.getType());

        // Return Config to Main Thread
        runOnUiThread(this::onConfigLoaded);
      } catch (IOException e) {
        e.printStackTrace();
        runOnUiThread(() -> onFailedToLoadConfig(e));
      }
    }).start();
  }

  private void onConfigLoaded() {
    // Set Objs
    App.mGeofenceConfig = mGeofenceList;

    // Calculate Splash Screen delay (if we do our calcs too quickly add a delay for a smoother ux)
    long diff = (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - mStartTime));
    long delay = (2 - diff) * 1000;
    new Handler().postDelayed(() -> {
      startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
      finish();
    }, delay);

  }

  private void onFailedToLoadConfig(Exception error) {
    CustomDialog.showCustomDialog(this,
        DialogType.ERROR,
        "Config Error",
        "Failed to load config file: " + error.getMessage(),
        "OK", (dialog, which) -> finish(),
        null, null,
        null, null,
        false);
  }

}