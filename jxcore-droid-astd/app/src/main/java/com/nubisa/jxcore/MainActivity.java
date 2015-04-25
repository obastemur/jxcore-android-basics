// License information is available from LICENSE file

package com.nubisa.jxcore;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends FragmentActivity {

    public static void callback(long id) throws Exception {
        jxcore.JXType tp = jxcore.JXType.fromInt(AppManager.currentActivity.jx_handler.getType(id));

        String json = "";
        switch(tp) {
            case RT_Int32:
                json += AppManager.currentActivity.jx_handler.getInt32(id);
                break;
            case RT_Double:
                json += AppManager.currentActivity.jx_handler.getDouble(id);
                break;
            case RT_Boolean:
                json += AppManager.currentActivity.jx_handler.getBoolean(id) == 1;
                break;
            case RT_String:
                json += AppManager.currentActivity.jx_handler.getString(id);
                break;
            case RT_JSON:
                json += AppManager.currentActivity.jx_handler.getString(id);
                break;
            case RT_Buffer:
                json += AppManager.currentActivity.jx_handler.getString(id);
                break;
            case RT_Undefined:
                json += "undefined";
                break;
            case RT_Null:
                json += "null";
                break;
            case RT_Error:
                json += AppManager.currentActivity.jx_handler.getString(id);
                break;
            default:
                json += AppManager.currentActivity.jx_handler.convertToString(id);

        }

        JXWebBridge.callbacks.add(json);
        AppManager.currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                web.loadUrl("javascript:jx_utils.checkCallbacks()");
            }
        });
    }

    public static JXWebView web = null;
    public jxcore jx_handler = null;
    public List jx_instructions;

    @SuppressLint("SetJavaScriptEnabled")
    public static void JXCoreAssetsReady() { // assets are sent. call start event
        FrameLayout layout = (FrameLayout) AppManager.currentActivity
                .findViewById(R.id.container);

        web = new JXWebView(AppManager.currentContext);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.setWebChromeClient(new JXClient());
        web.setJXcoreInterface(new JXWebBridge());

        layout.addView(web);

        web.loadHTML("file:///android_asset/ui/index.html", "home");
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        AppManager.currentActivity = this;
        AppManager.currentContext = this.getBaseContext();

        instance_alive = true;
        jx_handler = new jxcore();
        jx_handler.setNativeContext(AppManager.currentContext,
                AppManager.currentContext.getAssets());

        startProgress();
    }

    private Boolean instance_alive = false;

    public void startProgress() {
        jx_instructions = Collections.synchronizedList(new ArrayList<String>());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                jx_handler.Initialize(AppManager.currentContext.getFilesDir().getAbsolutePath());

                AppManager.currentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JXCoreAssetsReady();
                    }
                });

                while (instance_alive) {
                    try {
                        int active = jx_handler.loopOnce();
                        if (jx_instructions.isEmpty())
                            Thread.sleep(active == 1 ? 0 : 5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (!jx_instructions.isEmpty()) {
                        String json = (String) jx_instructions.remove(0);
                        long x = jx_handler.evalEngine(json);
                        if (x!=-2) {
                            String result = jx_handler.convertToString(x);
                            // unexpected return, is that an exception ?
                            Log.e("JXcore", result);
                        }
                        jx_handler.loopOnce();
                    }
                }
            }
        };
        new Thread(runnable).start();
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
        instance_alive = false;
    }

}
