// License information is available from LICENSE file

package com.nubisa.jxcore;

import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

public class JXClient extends WebChromeClient {
    @Override
    public boolean onJsAlert(WebView view, String url, String message,
                             JsResult result) {
        url = url.trim() + " "; // hack
        return super.onJsAlert(view, url, message, result);
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier,
                                        long currentQuota, long estimatedSize, long totalUsedQuota,
                                        WebStorage.QuotaUpdater quotaUpdater) {
        quotaUpdater.updateQuota(estimatedSize * 2);
    }
}
