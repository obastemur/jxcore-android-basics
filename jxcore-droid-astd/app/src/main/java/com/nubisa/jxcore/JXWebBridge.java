// License information is available from LICENSE file

package com.nubisa.jxcore;

import java.util.ArrayList;

import android.webkit.JavascriptInterface;

public class JXWebBridge {

    public String cbName = "null";
    public JXWebView view = null;

    static long callId;

    static {
        callId = 0;
    }

    @JavascriptInterface
    public long uniqueId() {
        long id = ++callId;
        if (id >= Long.MAX_VALUE) {
            id = 1;
            callId = 0;
        }

        return id; // id shouldn't be 0! (JS side ! check)
    }

    @JavascriptInterface
    public void call(final String json) {
        AppManager.currentActivity.jx_instructions.add(json);
    }

    public static ArrayList<String> callbacks = new ArrayList<String>();

    @JavascriptInterface
    public String getCallback() {
        if (callbacks.size() > 0)
            return callbacks.remove(0);
        else
            return null;
    }
}
