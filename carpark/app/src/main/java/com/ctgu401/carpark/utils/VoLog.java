package com.ctgu401.carpark.utils;

import android.util.Log;

public class VoLog {
    private static final String TAG = "CarPark";

    public static void e(String tag, String msg) {
        Log.e(TAG, tag + ":::" + msg);
    }

    public static void w(String tag, String msg) {
        Log.w(TAG, tag + ":::" + msg);
    }

    public static void d(String tag, String msg) {
        Log.d(TAG, tag + ":::" + msg);
    }

    public static void i(String tag, String msg) {
        Log.i(TAG, tag + ":::" + msg);
    }

    public static void v(String tag, String msg) {
        Log.v(TAG, tag + ":::" + msg);
    }
}
