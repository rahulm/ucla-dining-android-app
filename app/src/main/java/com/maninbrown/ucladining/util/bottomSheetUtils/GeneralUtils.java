package com.maninbrown.ucladining.util.bottomSheetUtils;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.maninbrown.ucladining.R;
import com.maninbrown.ucladining.util.DebugUtils;
import com.maninbrown.ucladining.util.TypefaceUtil;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Rahul on 9/18/2015.
 */
public class GeneralUtils {

    private static final String TAG = "GeneralUtils";

    private static void logDebug(String message) {
        DebugUtils.logDebug(TAG, message);
    }


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

    public static DatePicker getInflatedBottomSheetDatePicker(Activity activity, DateTime currentDate, DatePicker.OnDateChangedListener onDateChangedListener) {
        DatePicker datePicker = new DatePicker(activity);
        datePicker.setCalendarViewShown(false);
        datePicker.setSpinnersShown(true);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        datePicker.setLayoutParams(layoutParams);


        int day, month, year;
        if (currentDate == null) {
            currentDate = DateTime.now();
        }
        day = currentDate.getDayOfMonth();
        month = currentDate.getMonthOfYear() - 1;
        year = currentDate.getYear();
        datePicker.init(year, month, day, onDateChangedListener);

        return datePicker;
    }

    public static LinearLayout getInflatedBottomSheetMealPickerLayout(Activity activity, List<String> items, final String currOptions, AdapterView.OnItemSelectedListener onItemSelectedListener) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_meal_picker, null);
//        ((TextView) linearLayout.findViewById(R.id.bottom_sheet_meal_text)).setTypeface(TypefaceUtil.getBold(activity));
        final Spinner spinner = (Spinner) linearLayout.findViewById(R.id.bottom_sheet_meal_spinner);

        final ArrayAdapter<String> spinnerAdapter = new CustomSpinnerAdapter(activity, R.layout.meal_spinner_item);
        if (items != null) {
            spinnerAdapter.addAll(items);
        }
        spinnerAdapter.setDropDownViewResource(R.layout.meal_spinner_item);
        spinner.setAdapter(spinnerAdapter);


        if (onItemSelectedListener != null) {
            spinner.setOnItemSelectedListener(onItemSelectedListener);
        }

        if (currOptions != null && !currOptions.isEmpty()) {
            spinner.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        int position = spinnerAdapter.getPosition(currOptions);
                        if (position >= 0 && position < spinnerAdapter.getCount()) {
                            spinner.setSelection(position);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return linearLayout;
    }
}
