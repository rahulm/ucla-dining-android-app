package com.maninbrown.ucladining.util.bottomSheetUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.maninbrown.ucladining.R;
import com.maninbrown.ucladining.util.TypefaceUtil;

import java.util.List;

/**
 * Created by Rahul on 9/18/2015.
 */
public class CustomSpinnerAdapter extends ArrayAdapter<String> {


    public CustomSpinnerAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CustomSpinnerAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public CustomSpinnerAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    public CustomSpinnerAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public CustomSpinnerAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    public CustomSpinnerAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//            return super.getView(position, convertView, parent);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.meal_spinner_item, null);
        }
        TextView textView = (TextView) convertView;
        textView.setText(getItem(position));
        textView.setTypeface(TypefaceUtil.getItalic(getContext()));

        return convertView;
    }
}