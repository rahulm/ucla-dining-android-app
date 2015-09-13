package com.maninbrown.ucladining.fragments;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maninbrown.ucladining.MainActivity;
import com.maninbrown.ucladining.R;

/**
 * Base fragment.
 *
 * Created by Rahul on 9/13/2015.
 */
public abstract class BaseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static View mRootView;

    private static SwipeRefreshLayout mSwipeRefreshLayout;

    private static RecyclerView mRecyclerViewLayout;

    private static int mLayoutId;

    private String mToolbarTitleText = "UCLA Dining";

    private boolean mBackButtonIsOn = true;

    private boolean mRefreshButtonIsOn = true;



    protected void setRootView(View view) {
        mRootView = view;
    }

    protected View getRootView() {
        return mRootView;
    }


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

    protected void setLayoutId(int layoutId) {
        mLayoutId = layoutId;
    }

    private void findSwipeRefreshLayout() {
        if (mRootView!=null) {
            if (mRootView instanceof SwipeRefreshLayout) {
                mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView;
            } else {
                mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.shared_swipe_refresh_layout);
            }
            if (mSwipeRefreshLayout != null)
                mSwipeRefreshLayout.setOnRefreshListener(this);
        }
    }

    /**
     * Called by SwipeRefreshLayout and can be overridden in subclasses.
     */
    @Override
    public void onRefresh(){
        showSwipeRefresh(); // TODO: check if this should be here
        doRefresh(new RefreshListener() {
            @Override
            public void OnRefreshComplete() {
                hideSwipeRefresh();
            }
        });
    }

    protected void hideSwipeRefresh() {
        if (mSwipeRefreshLayout!=null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    protected void showSwipeRefresh() {
        if (mSwipeRefreshLayout!=null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private static RecyclerView.LayoutManager mLayoutManager;
    private static RecyclerView.ItemDecoration mItemDecoration;
    private static RecyclerView.ItemAnimator mItemAnimator;
    private static RecyclerView.OnItemTouchListener mRecycleOnItemTouchListener;

    private void findRecyclerView() {
        if (mRootView!=null) {
            if (mRootView instanceof RecyclerView) {
                mRecyclerViewLayout = (RecyclerView) mRootView;
            } else {
                mRecyclerViewLayout = (RecyclerView) mRootView.findViewById(R.id.shared_recycler_layout);
            }

            if (mLayoutManager==null) {
                mLayoutManager = new LinearLayoutManager(getActivity());
            }
            if (mItemDecoration!=null) {
                mRecyclerViewLayout.removeItemDecoration(mItemDecoration);
            } else {
                mItemDecoration = new SimpleItemDecoration();
            }
            if (mItemAnimator==null){
                mItemAnimator = new DefaultItemAnimator();
            }

            mRecyclerViewLayout.setLayoutManager(mLayoutManager);
            mRecyclerViewLayout.addItemDecoration(mItemDecoration);
            mRecyclerViewLayout.setItemAnimator(mItemAnimator);

            mRecyclerViewLayout.setClickable(true);
        }
    }

    private class SimpleItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = 10;
            outRect.right = 10;
            outRect.top = 5;
            outRect.bottom = 5;
        }
    }

    protected void setRecyclerAdapter(RecyclerView.Adapter adapter) {
        if (mRecyclerViewLayout!=null) {
            mRecyclerViewLayout.setAdapter(adapter);
            mRecyclerViewLayout.setClickable(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        if (mRootView==null || mSwipeRefreshLayout==null || mRecyclerViewLayout==null) {
            mRootView = inflater.inflate(mLayoutId, null, false);
            findSwipeRefreshLayout(); findRecyclerView();
        }

        populateRootView();

        return mRootView;
    }


    protected abstract void populateRootView();

    @Override
    public void onResume() {
        super.onResume();
        refreshToolbarTitleText();
        refreshBackButton();
        refreshToolbarRefreshButton();
        findSwipeRefreshLayout();
        findRecyclerView();
    }

    protected MainActivity getMainActivity() {
        Activity activity = getActivity();
        return (activity == null) ? null : (MainActivity) activity;
    }


    // Abstract methods

    public abstract class RefreshListener {
        public abstract void OnRefreshComplete();
    }

    public abstract void doRefresh(@Nullable RefreshListener refreshListener);

}
