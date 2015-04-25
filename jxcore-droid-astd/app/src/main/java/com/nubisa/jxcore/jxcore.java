package com.nubisa.jxcore;

import android.content.Context;
import android.content.res.AssetManager;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class jxcore {

    static {
        System.loadLibrary("jxcore");
    }

    public enum JXType {
        RT_Int32(1),
        RT_Double(2),
        RT_Boolean(3),
        RT_String(4),
        RT_JSON(5),
        RT_Buffer(6),
        RT_Undefined(7),
        RT_Null(8),
        RT_Error(9),
        RT_Function(10),
        RT_Object(11),
        RT_Unsupported(12);

        int val;
        private JXType(int n) {
          val = n;
        }

        public static JXType fromInt(int n) {
            switch(n) {
                case 1:
                  return RT_Int32;
                case 2:
                    return RT_Double;
                case 3:
                    return RT_Boolean;
                case 4:
                    return RT_String;
                case 5:
                    return RT_JSON;
                case 6:
                    return RT_Buffer;
                case 7:
                    return RT_Undefined;
                case 8:
                    return RT_Null;
                case 9:
                    return RT_Error;
                default:
                    return RT_Unsupported;
            }
        }
    }

    public native void setNativeContext(final Context context,
                                        final AssetManager assetManager);

    public native int loopOnce();

    public native void startEngine();

    public native void prepareEngine(String home, String fileTree);

    public native void stopEngine();

    public native void defineMainFile(String content);

    public native long evalEngine(String script);

    public native int getType(long id);

    public native double getDouble(long id);

    public native String getString(long id);

    public native int getInt32(long id);

    public native int getBoolean(long id);

    public native String convertToString(long id);


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
