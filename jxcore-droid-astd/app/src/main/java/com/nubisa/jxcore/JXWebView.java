// License information is available from LICENSE file

package com.nubisa.jxcore;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

public class JXWebView extends WebView {

    public JXWebView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    JXWebBridge bridge = null;

    public void setJXcoreInterface(JXWebBridge target) {
        bridge = target;
        bridge.view = this;
        addJavascriptInterface(bridge, "_jxcore_");
    }

    public void loadHTML(String url, String cbName) {
        bridge.cbName = cbName;

        loadUrl(url);
    }
}
