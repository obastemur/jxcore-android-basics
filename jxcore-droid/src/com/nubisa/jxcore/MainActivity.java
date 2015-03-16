// License information is available from LICENSE file

package com.nubisa.jxcore;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MainActivity extends FragmentActivity {

  public static void callback(String json) throws Exception {
    JXWebBridge.callbacks.add(json);
    AppManager.currentActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        web.loadUrl("javascript:jx_utils.checkCallbacks()");
      }
    });
  }

  static {
    System.loadLibrary("jxcore");
  }

  public native void sendMessage(String message);

  public native void setNativeContext(final Context context,
      final AssetManager assetManager);

  public native int loopOnce();

  public native void startEngine();

  public native void prepareEngine(String home, String fileTree);

  public native void stopEngine();

  public native void defineFile(String filename, String content);

  public native void defineMainFile(String content);

  public native String evalEngine(String script);

  public static boolean jxcoreClosed = false;
  public static JXWebView web = null;

  @SuppressLint("SetJavaScriptEnabled")
  public static void JXCoreAssetsReady() { // assets are sent. call start event
    FrameLayout layout = (FrameLayout) AppManager.currentActivity
        .findViewById(R.id.container);

    web = new JXWebView(AppManager.currentContext);
    web.getSettings().setJavaScriptEnabled(true);
    web.getSettings().setDomStorageEnabled(true);
    web.setWebChromeClient(new JXClient());
    web.setWebViewClient(new JXViewClient());
    web.setJXcoreInterface(new JXWebBridge());

    layout.addView(web);

    web.loadHTML("file:///android_asset/ui/index.html", "home");
  }

  private Handler handler = null;

  @SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_main);

    AppManager.currentActivity = this;
    AppManager.currentContext = this.getBaseContext();
    setNativeContext(AppManager.currentContext,
        AppManager.currentContext.getAssets());

    Initialize(AppManager.currentContext.getFilesDir().getAbsolutePath());
  }

  void Initialize(String home) {
    // we could proxy it by request (over NDK) but NDK doesn't support reading
    // directory names.
    // we do not read the file contents here. just the name tree.
    // assets.list is terribly slow, below trick is literally 100 times faster
    StringBuilder assets = new StringBuilder();
    assets.append("{");
    boolean first_entry = true;
    try {
      ZipFile zf = new ZipFile(
          AppManager.currentContext.getApplicationInfo().sourceDir);
      try {
        for (Enumeration<? extends ZipEntry> e = zf.entries(); e
            .hasMoreElements();) {
          ZipEntry ze = e.nextElement();
          String name = ze.getName();
          if (name.startsWith("assets/jxcore/")) {
            if (first_entry)
              first_entry = false;
            else
              assets.append(",");
            int size = FileManager.aproxFileSize(name.substring(7));
            assets.append("\"" + name.substring(14) + "\":" + size);
          }
        }
      } finally {
        zf.close();
      }
    } catch (Exception e) {
    }
    assets.append("}");

    prepareEngine(home, assets.toString());

    String mainFile = FileManager.readFile("main.js");
    String data = "process.cwd = function(){ return '" + home + "';};\n"
        + mainFile;
    defineMainFile(data);

    startEngine();

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        int active = loopOnce();

        if (active == 0)
          handler.postDelayed(this, 10);
        else
          handler.postDelayed(this, 1);
      }
    };

    if (handler != null) {
      handler.getLooper().quit();
    }
    handler = new Handler();
    handler.postDelayed(runnable, 5);

    JXCoreAssetsReady();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.w("jxcore-MainActivity", "stop");
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.w("jxcore-MainActivity", "pause");
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.w("jxcore-MainActivity", "resume");

  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.w("jxcore-MainActivity", "restart");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.w("jxcore-MainActivity", "destroy");

    FrameLayout layout = (FrameLayout) AppManager.currentActivity
        .findViewById(R.id.container);
    layout.removeAllViews();

    web.destroy();
    web = null;
  }

}
