package com.maninbrown.ucladining.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import com.maninbrown.ucladining.MainActivity;

/**
 * Base fragment.
 *
 * Created by Rahul on 9/13/2015.
 */
public abstract class BaseFragment extends Fragment {

    private String mToolbarTitleText = "UCLA Dining";

    private boolean mBackButtonIsOn = true;

    private boolean mRefreshButtonIsOn = true;

    // Toolbar options
    protected void setToolbarTitle(String title) {
        mToolbarTitleText = title;
        refreshToolbarTitleText();
    }

    private void refreshToolbarTitleText() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity!=null) {
            mainActivity.setToolbarTitle(mToolbarTitleText);
            if (mToolbarTitleText.isEmpty()) {
                mainActivity.setToolbarTitleVisibility(View.INVISIBLE);
            }
        }
    }



    protected void setBackButtonOn(boolean isOn) {
        mBackButtonIsOn = isOn;
        refreshBackButton();
    }

    private void refreshBackButton() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity!=null) {
            mainActivity.toggleBackButton(mBackButtonIsOn);
        }
    }


    protected void setRefreshButtonIsOn(boolean isOn) {
        mRefreshButtonIsOn = isOn;
        refreshToolbarRefreshButton();
    }

    private void refreshToolbarRefreshButton() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity!=null) {
            mainActivity.toggleRefreshButton(mRefreshButtonIsOn);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshToolbarTitleText();
        refreshBackButton();
        refreshToolbarRefreshButton();
    }

    protected MainActivity getMainActivity() {
        Activity activity = getActivity();
        return (activity == null) ? null : (MainActivity) activity;
    }


    // Abstract methods

    public abstract void doRefresh();

}
