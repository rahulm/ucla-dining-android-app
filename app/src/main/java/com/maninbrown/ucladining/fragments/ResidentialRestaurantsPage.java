package com.maninbrown.ucladining.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maninbrown.ucladining.R;

/**
 * Created by Rahul on 9/13/2015.
 */
public class ResidentialRestaurantsPage extends BaseFragment {


    @Override
    public void doRefresh(RefreshListener refreshListener) {
        // TODO: refresh if not already refreshing
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("Residential Restaurants");
        setRefreshButtonIsOn(true);
        setBackButtonOn(true);
    }

    @Override
    protected void populateRootView() {
        // TODO:
    }


    //    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        //return super.onCreateView(inflater, container, savedInstanceState);
//
//        SwipeRefreshLayout layout = (SwipeRefreshLayout) inflater.inflate(R.layout.res_restaurants_list_page, null, false);
//        setRootView(layout);
//
//
//
//        return getRootView();
//    }
}
