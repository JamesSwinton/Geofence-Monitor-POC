package com.zebra.jamesswinton.geofencemonitorpoc;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.WindowManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.location.Geofence;
import com.zebra.jamesswinton.geofencemonitorpoc.data.CustomGeofence;
import com.zebra.jamesswinton.geofencemonitorpoc.utils.CustomDialog;
import com.zebra.jamesswinton.geofencemonitorpoc.utils.CustomDialog.DialogType;
import java.util.List;

public class DialogActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent launchIntent = getIntent();

    // Display
    displayDialog(launchIntent);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    displayDialog(intent);
  }

  private void dismissDialogIfPresent() {
    if (mWarningDialog != null && mWarningDialog.isShowing()) {
      mWarningDialog.dismiss();
      mWarningDialog = null;
    }
  }

  private AlertDialog mWarningDialog;
  private void displayDialog(Intent intent) {
    if (intent == null || !intent.hasExtra("geofence-transition-type")) {
      finish();
    }

    int transitionType = intent.getIntExtra("geofence-transition-type", -1);
    if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
      dismissDialogIfPresent();
      finish();
      return;
    }

    if (!intent.hasExtra("geofence-warning-title")
        || !intent.hasExtra("geofence-warning-message")) {
      finish();
      return;
    }

    // Get Intent Details
    String title = intent.getStringExtra("geofence-warning-title");
    String message = intent.getStringExtra("geofence-warning-message");

    // Show Dialog
    mWarningDialog = CustomDialog.createCustomDialog(this, DialogType.WARN,
        title, message, "OK", null,
        null, null,
        null, null, true);
    mWarningDialog.setOnDismissListener(dialog -> finish());
    mWarningDialog.show();

    // Play Sound
    MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
    mediaPlayer.start();
  }
}