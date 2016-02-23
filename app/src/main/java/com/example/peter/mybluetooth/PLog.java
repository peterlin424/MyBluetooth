package com.example.peter.mybluetooth;

import android.util.Log;

/**
 * Created by Peter on 16/2/19.
 */
public class PLog {
    public static boolean Debug = Pub.ISDEBUG;
    public static String debug = " [DEBUG] ";

    public static void v(String tag, String msg) {
        if (Debug) {
            Log.v(tag, debug + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (Debug) {
            Log.i(tag, debug + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (Debug) {
            Log.d(tag, debug + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (Debug) {
            Log.w(tag, debug + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (Debug) {
            Log.e(tag, debug + msg);
        }
    }

    public static void v(String tag, String msg, Throwable t) {
        if (Debug) {
            Log.v(tag, debug + msg, t);
        }
    }

    public static void i(String tag, String msg, Throwable t) {
        if (Debug) {
            Log.i(tag, debug + msg, t);
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (Debug) {
            Log.d(tag, debug + msg, t);
        }
    }

    public static void w(String tag, String msg, Throwable t) {
        if (Debug) {
            Log.w(tag, debug + msg, t);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (Debug) {
            Log.e(tag, debug + msg, t);
        }
    }
}
