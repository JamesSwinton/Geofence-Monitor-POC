package com.zebra.jamesswinton.geofencemonitorpoc.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.google.android.material.snackbar.Snackbar;
import com.zebra.jamesswinton.geofencemonitorpoc.data.CustomGeofence;
import com.zebra.jamesswinton.geofencemonitorpoc.R;
import com.zebra.jamesswinton.geofencemonitorpoc.databinding.LayoutEmptyBinding;
import com.zebra.jamesswinton.geofencemonitorpoc.databinding.LayoutGeofenceBinding;
import java.util.ArrayList;

public class GeofenceListAdapter extends RecyclerView.Adapter<ViewHolder> {

  // Data
  private Activity mCx;
  private ArrayList<CustomGeofence> mCustomGeofenceList;

  // View Types
  private static final int EmptyViewType = 0;
  private static final int GeofenceViewType = 1;

  public GeofenceListAdapter(Activity cx, ArrayList<CustomGeofence> customGeofenceList) {
    this.mCx = cx;
    this.mCustomGeofenceList = customGeofenceList;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ViewHolder viewHolder = null;
    switch (viewType) {
      case GeofenceViewType:
        return new GeofenceViewHolder(DataBindingUtil.inflate(LayoutInflater.from(mCx),
            R.layout.layout_geofence, parent, false));
      case EmptyViewType:
      default:
        return new EmptyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(mCx),
            R.layout.layout_empty, parent, false));
    }
  }

  @SuppressLint("DefaultLocale")
  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof GeofenceViewHolder) {
      CustomGeofence customGeofence = mCustomGeofenceList.get(position);
      GeofenceViewHolder vh = (GeofenceViewHolder) holder;
      if (position == 0) {
        vh.mDataBinding.divider.setVisibility(View.GONE);
      }

      vh.mDataBinding.label.setText(customGeofence.getLabel());
      vh.mDataBinding.desc.setText(customGeofence.getDesc());
      vh.mDataBinding.radius.setText(String.format("%1$d (m)", customGeofence.getRadius()));

      double lat = customGeofence.getLat();
      double lng = customGeofence.getLng();
      vh.mDataBinding.latlng.setText(String.format("%1$f, %2$f", lat, lng));
      vh.mDataBinding.latlng.setOnClickListener(v -> {
        Uri mapUri = Uri.parse(String.format("geo:0,0?q=%1$s,%2$s(%3$s)", lat, lng, customGeofence.getLabel()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mCx.startActivity(mapIntent);
      });
    }
  }

  public ArrayList<CustomGeofence> getGeofenceList() {
    return this.mCustomGeofenceList;
  }

  public void updateList(ArrayList<CustomGeofence> customGeofenceList) {
    this.mCustomGeofenceList = customGeofenceList;
    this.notifyDataSetChanged();
  }

  public void addGeofence(CustomGeofence customGeofence) {
    this.mCustomGeofenceList.add(customGeofence);
    this.notifyItemInserted(mCustomGeofenceList.size() - 1);
  }

  public void removeGeofence(int position) {
    mRecentlyDeletedItem = mCustomGeofenceList.get(position);
    mRecentlyDeletedItemPosition = position;
    mCustomGeofenceList.remove(position);
    notifyItemRemoved(position);
    showUndoSnackbar();
  }

  public void clearGeofences() {
    this.mCustomGeofenceList.clear();
    this.notifyDataSetChanged();
  }

  private void showUndoSnackbar() {
    View view = mCx.findViewById(R.id.base_layout);
    Snackbar snackbar = Snackbar.make(view, "Undo deletion of Geofence", Snackbar.LENGTH_LONG);
    snackbar.setAction("Undo", v -> undoDelete());
    snackbar.show();
  }


  int mRecentlyDeletedItemPosition;
  CustomGeofence mRecentlyDeletedItem;
  private void undoDelete() {
    mCustomGeofenceList.add(mRecentlyDeletedItemPosition, mRecentlyDeletedItem);
    notifyItemInserted(mRecentlyDeletedItemPosition);
  }


  @Override
  public int getItemViewType(int position) {
    return mCustomGeofenceList == null || mCustomGeofenceList.isEmpty() ? EmptyViewType : GeofenceViewType;
  }

  @Override
  public int getItemCount() {
    return mCustomGeofenceList == null || mCustomGeofenceList.isEmpty() ? 1 : mCustomGeofenceList.size();
  }

  private class GeofenceViewHolder extends ViewHolder {
    private LayoutGeofenceBinding mDataBinding;
    public GeofenceViewHolder(@NonNull LayoutGeofenceBinding binding) {
      super(binding.getRoot());
      this.mDataBinding = binding;
    }
  }

  private class EmptyViewHolder extends ViewHolder {
    private LayoutEmptyBinding mDataBinding;
    public EmptyViewHolder(@NonNull LayoutEmptyBinding binding) {
      super(binding.getRoot());
      this.mDataBinding = binding;
    }
  }
}
