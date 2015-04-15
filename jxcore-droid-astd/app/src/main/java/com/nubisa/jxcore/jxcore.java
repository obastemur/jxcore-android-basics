package com.nubisa.jxcore;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Gonzo on 4/15/15.
 */
public class jxcore {

    private Handler handler = null;

    static {
        System.loadLibrary("jxcore");
    }

    public native void setNativeContext(final Context context,
                                        final AssetManager assetManager);

    public native int loopOnce();

    public native void startEngine();

    public native void prepareEngine(String home, String fileTree);

    public native void stopEngine();

    public native void defineMainFile(String content);

    public native String evalEngine(String script);


    public void Initialize(String home) {
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
                        .hasMoreElements(); ) {
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
    }
}
