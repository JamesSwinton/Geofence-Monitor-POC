package com.zebra.jamesswinton.geofencemonitorpoc;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.zebra.jamesswinton.geofencemonitorpoc.adapters.GeofenceListAdapter;
import com.zebra.jamesswinton.geofencemonitorpoc.adapters.SwipeToDeleteCallback;
import com.zebra.jamesswinton.geofencemonitorpoc.data.CustomGeofence;
import com.zebra.jamesswinton.geofencemonitorpoc.databinding.ActivityMainBinding;
import com.zebra.jamesswinton.geofencemonitorpoc.databinding.LayoutAddGeofenceBinding;
import com.zebra.jamesswinton.geofencemonitorpoc.utils.FileUtils;
import com.zebra.jamesswinton.geofencemonitorpoc.utils.FileUtils.AssetFileUtils;
import com.zebra.jamesswinton.geofencemonitorpoc.utils.PermissionsHelper;
import com.zebra.jamesswinton.geofencemonitorpoc.utils.PermissionsHelper.OnPermissionsResultListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnPermissionsResultListener {

  // UI
  private ActivityMainBinding mDataBinding;

  // Permissions
  private PermissionsHelper mPermissionsHelper;

  // Adapter
  private GeofenceListAdapter mGeofenceAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    mDataBinding.addGeofence.hide();
    mDataBinding.startMonitoring.hide();
    mPermissionsHelper = new PermissionsHelper(this, this);
    mGeofenceAdapter = new GeofenceListAdapter(this, App.mGeofenceConfig);
    mDataBinding.locations.setLayoutManager(new LinearLayoutManager(this));
    mDataBinding.locations.setAdapter(mGeofenceAdapter);
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
        new SwipeToDeleteCallback(this, mGeofenceAdapter)
    );
    itemTouchHelper.attachToRecyclerView(mDataBinding.locations);

    mDataBinding.addGeofence.setOnClickListener(v -> showCreateNewGeofencePrompt());
    mDataBinding.startMonitoring.setOnClickListener(v -> {
      if (mGeofenceAdapter.getGeofenceList().isEmpty()) {
        Toast.makeText(this, "Please add a geofence first", Toast.LENGTH_LONG).show();
        return;
      }

      // Store Geofences
      saveGeofenceToFile();

      // Start Service
      Intent startMonitoringIntent = new Intent(this, MonitorGeofenceService.class);
      startMonitoringIntent.setAction(MonitorGeofenceService.LaunchIntentAction);
      startMonitoringIntent.putExtra(MonitorGeofenceService.GeofenceListExtraKey,
          mGeofenceAdapter.getGeofenceList());
      startService(startMonitoringIntent);
      finish();
    });
    mDataBinding.expandFab.setOnClickListener(v -> toggleFabs());
  }

  @Override
  public void onPermissionsGranted() {
    Toast.makeText(this, "Permissions Granted", Toast.LENGTH_LONG).show();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    mPermissionsHelper.onRequestPermissionsResult();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    mPermissionsHelper.onActivityResult();
  }

  private void showCreateNewGeofencePrompt() {
    LayoutAddGeofenceBinding addGeofenceBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
        R.layout.layout_add_geofence, (ViewGroup) mDataBinding.getRoot(), false);
    AlertDialog createGeofenceDialog = new AlertDialog.Builder(this)
        .setTitle("Add Geofence")
        .setView(addGeofenceBinding.getRoot())
        .setPositiveButton("Add", null)
        .setNegativeButton("Cancel", null)
        .create();
    createGeofenceDialog.show();
    createGeofenceDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
      if (verifyDialogInputs(addGeofenceBinding)) {
        mGeofenceAdapter.addGeofence(new CustomGeofence(
            addGeofenceBinding.label.getText().toString(),
            addGeofenceBinding.desc.getText().toString(),
            Double.parseDouble(addGeofenceBinding.latitude.getText().toString()),
            Double.parseDouble(addGeofenceBinding.longitude.getText().toString()),
            Integer.valueOf(addGeofenceBinding.radius.getText().toString())
        ));
        createGeofenceDialog.dismiss();
      }
    });
  }

  private boolean verifyDialogInputs(LayoutAddGeofenceBinding binding) {
    boolean valid = true;
    if (TextUtils.isEmpty(binding.label.getText())) {
      valid = false;
      binding.label.setError("Please enter a label");
    }

    if (TextUtils.isEmpty(binding.latitude.getText())) {
      valid = false;
      binding.latitude.setError("Please enter a latitude");

    }

    if (TextUtils.isEmpty(binding.longitude.getText())) {
      valid = false;
      binding.longitude.setError("Please enter a longitude");

    }

    if (TextUtils.isEmpty(binding.radius.getText())) {
      valid = false;
      binding.radius.setError("Please enter a radius");
    }

    if (TextUtils.isEmpty(binding.desc.getText())) {
      valid = false;
      binding.desc.setError("Please enter a description");
    }

    if (valid) {
      binding.label.setError(null);
      binding.latitude.setError(null);
      binding.longitude.setError(null);
      binding.radius.setError(null);
    }

    return valid;
  }

  boolean fabsShowing = false;
  private void toggleFabs() {
    if (fabsShowing) {
      mDataBinding.addGeofence.show();
      mDataBinding.startMonitoring.show();
      mDataBinding.addGeofence.animate().translationY(-(mDataBinding.startMonitoring.getCustomSize() + mDataBinding.expandFab.getCustomSize()));
      mDataBinding.startMonitoring.animate().translationY(-(mDataBinding.expandFab.getCustomSize()));
      mDataBinding.expandFab.setImageResource(R.drawable.ic_more_vert);
      fabsShowing = false;
    } else {
      mDataBinding.addGeofence.hide();
      mDataBinding.startMonitoring.hide();
      mDataBinding.addGeofence.animate().translationY(0);
      mDataBinding.startMonitoring.animate().translationY(0);
      mDataBinding.expandFab.setImageResource(R.drawable.ic_more);
      fabsShowing = true;
    }
  }

  private void saveGeofenceToFile() {
    new Thread(() -> {
      // Loop Files and Parse to POJOs
      try {
        File geofenceConfigFile = new File(getExternalFilesDir(null) + "/" + SplashScreenActivity.GeofenceConfigFileName);
        if (!geofenceConfigFile.exists()) {
          boolean fileCreated = geofenceConfigFile.createNewFile();
        }

        // Convert List to Json
        String geofenceJson = new Gson().toJson(mGeofenceAdapter.getGeofenceList());

        // Write to File
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(geofenceConfigFile));
        outputStreamWriter.write(geofenceJson);
        outputStreamWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
        runOnUiThread(() ->
            Toast.makeText(this, "Could not save Geofence List to file", Toast.LENGTH_LONG)
                .show());
      }
    }).start();
  }

}