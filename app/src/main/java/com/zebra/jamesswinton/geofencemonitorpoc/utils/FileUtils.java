package com.zebra.jamesswinton.geofencemonitorpoc.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

  public static void CopyInputStreamToOutputStream(InputStream in, OutputStream out)
      throws IOException {
    byte[] buffer = new byte[1024];
    int read;
    while((read = in.read(buffer)) != -1){
      out.write(buffer, 0, read);
    }
  }

  public static boolean isPackageInstalled(Context cx, String packageName) {
    try {
      PackageManager packageManager = cx.getPackageManager();
      packageManager.getPackageInfo(packageName, 0);
      return true;
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }
  }

  public static class AssetFileUtils {

    public static File CopyAssetToFile(Context cx, String assetName, File outFile) throws IOException {
      AssetManager assetManager = cx.getAssets();
      InputStream in = null;
      OutputStream out = null;
      try {
        in = assetManager.open(assetName);
        if (!outFile.exists()) {
          boolean created = outFile.createNewFile();
          if (!created) {
            throw new IOException("Could not create Output File: " + outFile.getAbsolutePath());
          }
        }
        out = new FileOutputStream(outFile);
        FileUtils.CopyInputStreamToOutputStream(in, out);
      } finally {
        if (in != null) { in.close(); }
        if (out != null) { out.close(); }
      } return outFile;
    }
  }

}
