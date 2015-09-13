package com.maninbrown.ucladining;

import android.app.Application;

import com.maninbrown.ucladining.util.TypefaceUtil;

/**
 * Created by Rahul on 9/12/2015.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        setUpFonts();
    }

    private void setUpFonts() {
        TypefaceUtil.overrideFont(this, "DEFAULT", "fonts/Arvo/Arvo-Regular.ttf");
        TypefaceUtil.overrideFont(this, "SANS_SERIF" , "fonts/Arvo/Arvo-Bold.ttf");
        TypefaceUtil.overrideFont(this, "SERIF" , "fonts/Arvo/Arvo-Italic.ttf");
        TypefaceUtil.overrideFont(this, "MONOSPACE", "fonts/Arvo/Arvo-BoldItalic.ttf");
    }
}
