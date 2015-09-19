package com.maninbrown.ucladining.util.bottomSheetUtils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.maninbrown.ucladining.R;
import com.maninbrown.ucladining.util.TypefaceUtil;

/**
 * Created by Rahul on 9/18/2015.
 */
public class GeneralUtils {


    public static View getInflatedBottomSheetTitleView(final Activity activity, View.OnClickListener onExitClickListener) {
        View titleLayout = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_title, null, false);
        TextView textView = (TextView) titleLayout.findViewById(R.id.bottom_sheet_title_view);
        textView.setTypeface(TypefaceUtil.getBold(activity));
        ImageButton exitButton = (ImageButton) titleLayout.findViewById(R.id.bottom_sheet_title_exit_button);
        if (onExitClickListener != null) {
            exitButton.setOnClickListener(onExitClickListener);
        }

        return titleLayout;
    }
}
