package com.maninbrown.ucladining.fragments;

import android.support.annotation.Nullable;

/**
 * Created by Rahul on 9/15/2015.
 */
public class QuickServiceRestaurantsListPage extends BaseFragment {
    protected static final String TAG = "QuickServicesPage";

    private boolean isRefreshing = false;

    @Override
    protected void populateRootView() {

    }

    @Override
    public void doRefresh(@Nullable RefreshListener refreshListener) {
        if (!isRefreshing) {
            isRefreshing = true;
        }
    }
}
