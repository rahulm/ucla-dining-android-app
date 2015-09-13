package com.maninbrown.ucladining.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maninbrown.ucladining.R;

/**
 * Created by Rahul on 9/13/2015.
 */
public class HomeOptionsPage extends BaseFragment {

    private Typeface mTypeface;

    @Override
    public void doRefresh() {
        // TODO:nothing
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("UCLA Dining");
//        setBackButtonOn(false);
//        setRefreshButtonIsOn(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.home_options_page_layout, null, false);
        setRootView(layout);

        if (mTypeface==null) {
            mTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Arvo/Arvo-Bold.ttf");
        }

        CardView cardView;
        TextView textView;

        cardView = (CardView) layout.findViewById(R.id.home_options_page_1);
        textView = (TextView) cardView.findViewById(R.id.home_option_item_text);
        textView.setSelected(true); textView.setTypeface(mTypeface);
        textView.setText("Residential Restaurants");
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Residential Restaurants pressed!", Toast.LENGTH_SHORT).show();
            }
        });

        cardView = (CardView) layout.findViewById(R.id.home_options_page_2);
        textView = (TextView) cardView.findViewById(R.id.home_option_item_text);
        textView.setSelected(true); textView.setTypeface(mTypeface);
        textView.setText("Quick Service Restaurants");
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Quick Service Restaurants pressed!", Toast.LENGTH_SHORT).show();
            }
        });

        cardView = (CardView) layout.findViewById(R.id.home_options_page_3);
        textView = (TextView) cardView.findViewById(R.id.home_option_item_text);
        textView.setSelected(true); textView.setTypeface(mTypeface);
        textView.setText("Restaurant Hours");
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Restaurant Hours pressed!", Toast.LENGTH_SHORT).show();
            }
        });

        return getRootView();
    }
}
