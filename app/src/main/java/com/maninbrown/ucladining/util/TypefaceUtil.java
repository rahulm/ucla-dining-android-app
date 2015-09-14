package com.maninbrown.ucladining.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Hashtable;

/**
 * Created by Rahul on 9/12/2015.
 */
public class TypefaceUtil {
    private static final String TAG = "TypefacUtil";

    /**
     * Using reflection to override default typeface
     * NOTICE: DO NOT FORGET TO SET TYPEFACE FOR APP THEME AS DEFAULT TYPEFACE WHICH WILL BE OVERRIDDEN
     * @param context to work with assets
     * @param defaultFontNameToOverride for example "monospace"
     * @param customFontFileNameInAssets file name of the font from assets
     */
    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
//            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

            final  Typeface customFontTypeface = get(customFontFileNameInAssets, context);
            if (customFontTypeface==null) {
                Log.e(TAG, "Can't find font: " + customFontFileNameInAssets + " to replace typeface: " + defaultFontNameToOverride);
            }

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            Log.e(TAG, "Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
        }
    }


    private static Hashtable<String, Typeface> fontCache = new Hashtable<>();

    public static Typeface get(String name, Context context) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }


    public static Typeface getRegular(Context context) {
        return get("fonts/Arvo/Arvo-Regular.ttf", context);
    }

    public static Typeface getBold(Context context) {
        return get("fonts/Arvo/Arvo-Bold.ttf", context);
    }

    public static Typeface getItalic(Context context) {
        return get("fonts/Arvo/Arvo-Italic.ttf", context);
    }

    public static Typeface getBoldItalic(Context context) {
        return get("fonts/Arvo/Arvo-BoldItalic.ttf", context);
    }
}