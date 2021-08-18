package com.zebra.jamesswinton.geofencemonitorpoc.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zebra.jamesswinton.geofencemonitorpoc.R;

public class CustomDialog {

  // Debugging
  private static final String TAG = "CustomDialog";

  // Constants
  public enum DialogType { SUCCESS, INFO, WARN, ERROR }

  // Static Variables


  // Variables

  public static void showCustomDialog(Context cx, DialogType type, String title, String message,
                                      String positiveButtonText, DialogInterface.OnClickListener positiveClickListener,
                                      String neutralButtonText, DialogInterface.OnClickListener neutralButtonClickListener,
                                      String negativeButtonText, DialogInterface.OnClickListener negativeClickListener,
                                      boolean dismissible) {

    // Inflate View
    View customDialogView = LayoutInflater.from(cx).inflate(R.layout.layout_custom_dialog, null);

    // Get View Components
    RelativeLayout headerLayout = customDialogView.findViewById(R.id.header_layout);
    ImageView headerIcon = customDialogView.findViewById(R.id.header_icon);
    TextView titleView = customDialogView.findViewById(R.id.title);
    TextView messageView = customDialogView.findViewById(R.id.message);

    // Set Component Values
    headerLayout.setBackgroundColor(getHeaderColor(cx, type));
    headerIcon.setImageDrawable(getHeaderIcon(cx, type));
    titleView.setText(Html.fromHtml(title));
    messageView.setText(Html.fromHtml(message));

    // Create Dialog
    AlertDialog customAlertDialog = new MaterialAlertDialogBuilder(cx)
            .setView(customDialogView)
            .setPositiveButton(positiveButtonText, positiveClickListener)
            .setNegativeButton(negativeButtonText, negativeClickListener)
            .setNeutralButton(neutralButtonText, neutralButtonClickListener)
            .setCancelable(dismissible)
            .create();

    // Show Dialog
    customAlertDialog.show();
  }

  public static AlertDialog createCustomDialog(Context cx, DialogType type, String title, String message,
      String positiveButtonText, DialogInterface.OnClickListener positiveClickListener,
      String neutralButtonText, DialogInterface.OnClickListener neutralButtonClickListener,
      String negativeButtonText, DialogInterface.OnClickListener negativeClickListener,
      boolean dismissible) {

    // Inflate View
    View customDialogView = LayoutInflater.from(cx).inflate(R.layout.layout_custom_dialog, null);

    // Get View Components
    RelativeLayout headerLayout = customDialogView.findViewById(R.id.header_layout);
    ImageView headerIcon = customDialogView.findViewById(R.id.header_icon);
    TextView titleView = customDialogView.findViewById(R.id.title);
    TextView messageView = customDialogView.findViewById(R.id.message);

    // Set Component Values
    headerLayout.setBackgroundColor(getHeaderColor(cx, type));
    headerIcon.setImageDrawable(getHeaderIcon(cx, type));
    titleView.setText(Html.fromHtml(title));
    messageView.setText(Html.fromHtml(message));

    // Create Dialog
    AlertDialog customAlertDialog = new MaterialAlertDialogBuilder(cx)
        .setView(customDialogView)
        .setPositiveButton(positiveButtonText, positiveClickListener)
        .setNegativeButton(negativeButtonText, negativeClickListener)
        .setNeutralButton(neutralButtonText, neutralButtonClickListener)
        .setCancelable(dismissible)
        .create();

    // Show Dialog
    return customAlertDialog;
  }

  public static AlertDialog createLoadingDialog(Context cx, String message) {
    // Inflate View
    View customDialogView = LayoutInflater.from(cx).inflate(R.layout.layout_loading_dialog, null);

    // Get View Components
    TextView messageView = customDialogView.findViewById(R.id.progress_message);

    // Set Component Values
    messageView.setText(Html.fromHtml(message));

    // Create Dialog
    return new MaterialAlertDialogBuilder(cx)
            .setView(customDialogView)
            .setCancelable(false)
            .create();
  }


  private static int getHeaderColor(Context cx, DialogType type) {
    int color = 0;
    switch (type) {
      case SUCCESS:
        color = ContextCompat.getColor(cx, R.color.success);
        break;
      case INFO:
        color = ContextCompat.getColor(cx, R.color.info);
        break;
      case WARN:
        color = ContextCompat.getColor(cx, R.color.warn);
        break;
      case ERROR:
        color = ContextCompat.getColor(cx, R.color.error);
        break;
    } return color;
  }

  private static Drawable getHeaderIcon(Context cx, DialogType type) {
    Drawable icon = null;
    switch (type) {
      case SUCCESS:
        icon = ContextCompat.getDrawable(cx, R.drawable.ic_success);
        break;
      case INFO:
        icon = ContextCompat.getDrawable(cx, R.drawable.ic_info);
        break;
      case WARN:
        icon = ContextCompat.getDrawable(cx, R.drawable.ic_warning);
        break;
      case ERROR:
        icon = ContextCompat.getDrawable(cx, R.drawable.ic_error);
        break;
    } return icon;
  }


}
