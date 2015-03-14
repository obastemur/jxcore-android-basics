// License information is available from LICENSE file

package com.nubisa.jxcore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

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

  public class FILE_OPS {
    public static final int assets = 0;
  }

  public static String GetFileNames(String location, String self) {
    StringBuilder sb = new StringBuilder();
    AssetManager amg = AppManager.currentContext.getAssets();
    sb.append("\"" + self + "\":");
    String[] lst = null;
    boolean has = false;
    try {
      lst = amg.list(location);
      for (int i = 0; i < lst.length; i++) {
        if (i != 0)
          sb.append(",");
        else
          sb.append("{");
        sb.append(GetFileNames(location + "/" + lst[i], lst[i]));
        has = true;
      }
    } catch (Exception e) {
    }
    if (!has) {
      sb.append("{\"f\":0, \"s\":");
      boolean is_open = false;
      InputStream afd = null;
      try {
        afd = amg.open(location);
        is_open = true;
      } catch (Exception e) {
      }
      if (is_open) {
        long sz = 1;
        try {
          sz = afd.available();
          afd.close();
        } catch (Exception e) {
        }
        sb.append(sz);
      } else
        sb.append("0");
      sb.append("}");
    } else
      sb.append(",\"f\":1}");

    return sb.toString();
  }
}
