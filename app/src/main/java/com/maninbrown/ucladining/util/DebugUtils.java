package com.maninbrown.ucladining.util;

import android.util.Log;

/**
 * Created by Rahul on 9/14/2015.
 */
public class DebugUtils {

    public static final boolean DEBUG_MODE_ON = false;

    public static void logDebug(String tag, String message) {
        if (DEBUG_MODE_ON)
            Log.d(tag, message);
    }
}
