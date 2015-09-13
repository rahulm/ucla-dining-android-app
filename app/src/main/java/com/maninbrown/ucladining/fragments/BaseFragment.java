package com.maninbrown.ucladining.fragments;

import android.support.v4.app.Fragment;

import com.maninbrown.ucladining.MainActivity;

/**
 * Base fragment.
 *
 * Created by Rahul on 9/13/2015.
 */
public abstract class BaseFragment extends Fragment {

    public abstract void doRefresh();

    protected MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

}
