// License information is available from LICENSE file

package com.nubisa.jxcore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class FileManager {

  public static String readFile(String location) {
    return readFile(location, "UTF-8");
  }

  public static String readFile(String location, String encoding) {
    StringBuilder sb = new StringBuilder();
    try {
      AssetManager asm = AppManager.currentContext.getAssets();
      BufferedReader br = new BufferedReader(new InputStreamReader(
          asm.open(location), encoding));

      String str = br.readLine();
      while (str != null) {
        sb.append(str + "\n");
        str = br.readLine();
      }

      br.close();
    } catch (IOException e) {
      Log.w("jxcore-FileManager", "readfile failed");
      e.printStackTrace();
      return null;
    }

    return sb.toString();
  }

  public static int aproxFileSize(String location) {
    int size = 0;
    try {
      AssetManager asm = AppManager.currentContext.getAssets();
      InputStream st = asm.open(location, AssetManager.ACCESS_UNKNOWN);
      size = st.available();
      st.close();
    } catch (IOException e) {
      Log.w("jxcore-FileManager", "aproxFileSize failed");
      e.printStackTrace();
      return 0;
    }

    return size;
  }

  public class FILE_OPS {
    public static final int assets = 0;
  }

  // do not use this, it's terribly slow (assets.list())
  public static String GetFileNames(String location, String self) {
    StringBuilder sb = new StringBuilder();
    AssetManager amg = AppManager.currentContext.getAssets();
    sb.append("\"" + self + "\":");
    String[] lst = null;
    try {
      lst = amg.list(location);
      for (int i = 0; i < lst.length; i++) {
        boolean is_dir = false;
        long size = 0;
        try {
          InputStream st = amg.open(location + "/" + lst[i],
              AssetManager.ACCESS_UNKNOWN);

          size = st.available();
          st.close();
        } catch (Exception e) {
          is_dir = true;
        }

        if (i != 0)
          sb.append(",");
        else
          sb.append("{");

        if (is_dir) {
          sb.append(GetFileNames(location + "/" + lst[i], lst[i]));
        } else
          sb.append("\"" + lst[i] + "\":{\"!s\":" + size + "}");
      }
    } catch (Exception e) {
    }

    sb.append("}");

    return sb.toString();
  }
}
