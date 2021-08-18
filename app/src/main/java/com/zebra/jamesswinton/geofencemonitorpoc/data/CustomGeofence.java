package com.zebra.jamesswinton.geofencemonitorpoc.data;

import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class CustomGeofence implements Serializable {

  private static final DecimalFormat df = new DecimalFormat("#.######");
  @SerializedName("label")
  @Expose
  private String label;
  @SerializedName("desc")
  @Expose
  private String desc;
  @SerializedName("lng")
  @Expose
  private double lng;
  @SerializedName("lat")
  @Expose
  private double lat;
  @SerializedName("radius")
  @Expose
  private int radius;

  public CustomGeofence(String label, String desc, double lng, double lat, int radius) {
    this.label = label;
    this.desc = desc;
    this.lng = lng;
    this.lat = lat;
    this.radius = radius;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public double getLat() {
    return round(lat);
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getLng() {
    return round(lng);
  }

  public void setLng(double lng) {
    this.lng = lng;
  }

  public int getRadius() {
    return radius;
  }

  public void setRadius(int radius) {
    this.radius = radius;
  }

  private double round(double number) {
    return number; //(double) Math.round(number * 10000000000d) / 10000000000d;
  }
}
