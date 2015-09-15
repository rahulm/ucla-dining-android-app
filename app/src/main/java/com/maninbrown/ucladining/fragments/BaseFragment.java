package com.maninbrown.ucladining.fragments;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maninbrown.ucladining.MainActivity;
import com.maninbrown.ucladining.R;

/**
 * Base fragment.
 * <p/>
 * Created by Rahul on 9/13/2015.
 */
public abstract class BaseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "BaseFragment";

    private View mRootView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerViewLayout;

    private CardView mEmptyView;

    private int mLayoutId;


    private String mToolbarTitleText = "UCLA Dining";

    private boolean mBackButtonIsOn = true;

    private boolean mRefreshButtonIsOn = true;

    private boolean mOptionsButtonIsOn = false;

    private View.OnClickListener mOptionsButtonOnClickListener = null;

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
        if (mainActivity != null) {
            Log.d(TAG, "refreshToolbarTitleText got inside to set toolbar text");
            mainActivity.setToolbarTitle(mToolbarTitleText);
            if (mToolbarTitleText.isEmpty()) {
                mainActivity.setToolbarTitleVisibility(View.INVISIBLE);
            } else {
                mainActivity.setToolbarTitleVisibility(View.VISIBLE);
            }
        }
    }


    protected void setBackButtonOn(boolean isOn) {
        mBackButtonIsOn = isOn;
        refreshBackButton();
    }

    private void refreshBackButton() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity != null) {
            mainActivity.toggleBackButton(mBackButtonIsOn);
        }
    }


    protected void setRefreshButtonIsOn(boolean isOn) {
        mRefreshButtonIsOn = isOn;
        refreshToolbarRefreshButton();
    }

    private void refreshToolbarRefreshButton() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity != null) {
            mainActivity.toggleRefreshButton(mRefreshButtonIsOn);
        }
    }


    protected void setOptionsButtonIsOn(boolean isOn, @Nullable View.OnClickListener onClickListener) {
        mOptionsButtonIsOn = isOn;
        mOptionsButtonOnClickListener = onClickListener;
        refreshOptionsButton();
    }

    private void refreshOptionsButton() {
        MainActivity mainActivity = getMainActivity();
        if (mainActivity != null) {
            mainActivity.toggleOptionsButton(mOptionsButtonIsOn, mOptionsButtonOnClickListener);
        }
    }

    protected void setLayoutId(int layoutId) {
        mLayoutId = layoutId;
    }

    private void findSwipeRefreshLayout() {
        getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRootView != null) {
                    if (mRootView instanceof SwipeRefreshLayout) {
                        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView;
                    } else {
                        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.shared_swipe_refresh_layout);
                    }
                    if (mSwipeRefreshLayout != null)
                        mSwipeRefreshLayout.setOnRefreshListener(BaseFragment.this);
                }
            }
        });
    }

    /**
     * Called by SwipeRefreshLayout and can be overridden in subclasses.
     */
    @Override
    public void onRefresh() {
        showSwipeRefresh(); // TODO: check if this should be here
        doRefresh(new RefreshListener() {
            @Override
            public void OnRefreshComplete() {
                hideSwipeRefresh();
            }
        });
    }

    protected boolean isLayoutRefreshing() {
        return mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing();
    }

    protected void hideSwipeRefresh() {
        getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    protected void showSwipeRefresh() {
        getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            }
        });
    }

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;
    private RecyclerView.ItemAnimator mItemAnimator;
//    private static RecyclerView.OnItemTouchListener mRecycleOnItemTouchListener;

    private void findRecyclerView() {
        getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRootView != null) {
                    if (mRootView instanceof RecyclerView) {
                        mRecyclerViewLayout = (RecyclerView) mRootView;
                    } else {
                        mRecyclerViewLayout = (RecyclerView) mRootView.findViewById(R.id.shared_recycler_layout);
                    }

//                    if (mLayoutManager == null) {
//                        mLayoutManager = new LinearLayoutManager(getActivity());
//                    }
                    if (mItemDecoration != null) {
                        if (mRecyclerViewLayout == null) {
                            Log.e(TAG, "findRecyclerView Recycler View is null!");
                        }
                        mRecyclerViewLayout.removeItemDecoration(mItemDecoration);
                    } else {
                        mItemDecoration = new SimpleItemDecoration();
                    }
//                    if (mItemAnimator == null) {
//                        mItemAnimator = new DefaultItemAnimator();
//                    }

//                    if (mRecyclerViewLayout.getLayoutManager()==null)
//                        mRecyclerViewLayout.setLayoutManager(mLayoutManager);
                    mRecyclerViewLayout.setLayoutManager(new LinearLayoutManager(getActivity()));

                    mRecyclerViewLayout.addItemDecoration(mItemDecoration);

//                    if (mRecyclerViewLayout.getItemAnimator()==null)
//                        mRecyclerViewLayout.setItemAnimator(mItemAnimator);
                    mRecyclerViewLayout.setItemAnimator(new DefaultItemAnimator());

                    mRecyclerViewLayout.setClickable(true);
                }
            }
        });
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

    protected void setRecyclerAdapter(final RecyclerView.Adapter adapter) {
        getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRecyclerViewLayout != null) {
                    if (adapter==null) {
                        mRecyclerViewLayout.setVisibility(View.INVISIBLE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                        mRecyclerViewLayout.setVisibility(View.VISIBLE);
                        mRecyclerViewLayout.setAdapter(adapter);
                        mRecyclerViewLayout.setClickable(true);
                    }
                } else {
                    Log.e(TAG, "setRecyclerAdapter Recycler view layout is null!");
                }
            }
        });
    }

    private static Typeface mBoldItalicTypeface;

    private void findEmptyView() {
        getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRootView!=null) {
                    mEmptyView = (CardView) mRootView.findViewById(R.id.shared_empty_view_card);
                    TextView textView = (TextView) mEmptyView.findViewById(R.id.shared_empty_view_text);
                    if (mBoldItalicTypeface==null) {
                        mBoldItalicTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Arvo/Arvo-BoldItalic.ttf");
                    }
                    textView.setTypeface(mBoldItalicTypeface);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
//        if (mRootView==null) {
//            mRootView = inflater.inflate(mLayoutId, container, false);
//        } else {
//            ((ViewGroup)mRootView.getParent()).removeView(mRootView);
//        }
//
//        if (mSwipeRefreshLayout==null) {
//            findSwipeRefreshLayout();
//        } else {
//            ((ViewGroup)mSwipeRefreshLayout.getParent()).removeView(mSwipeRefreshLayout);
//        }
//
//        if (mRecyclerViewLayout==null) {
//            findRecyclerView();
//        } else {
//            ((ViewGroup)mRecyclerViewLayout.getParent()).removeView(mRecyclerViewLayout);
//        }
//
//        if (mEmptyView==null) {
//            findEmptyView();
//        } else {
//            ((ViewGroup)mEmptyView.getParent()).removeView(mEmptyView);
//        }

//        if (mRootView!=null) {
//            ViewParent parent = mRootView.getParent();
//            if (parent!=null) {
//                ((ViewGroup)parent).removeView(mRootView);
//            }
//        }
//        if (mSwipeRefreshLayout!=null) {
//            ViewParent parent = mSwipeRefreshLayout.getParent();
//            if (parent!=null) {
//                ((ViewGroup)parent).removeView(mSwipeRefreshLayout);
//            }
//        }
//        if (mRecyclerViewLayout!=null) {
//            ViewParent parent = mRecyclerViewLayout.getParent();
//            if (parent!=null) {
//                ((ViewGroup)parent).removeView(mRecyclerViewLayout);
//            }
//        }
//        if (mEmptyView!=null) {
//            ViewParent parent = mEmptyView.getParent();
//            if (parent!=null) {
//                ((ViewGroup)parent).removeView(mEmptyView);
//            }
//        }

//        if (mRootView == null || mSwipeRefreshLayout == null || mRecyclerViewLayout == null || mEmptyView == null) {
//            mRootView = inflater.inflate(mLayoutId, null, false);
//            findSwipeRefreshLayout();
//            findRecyclerView();
//            findEmptyView();
//        }


        mRootView = inflater.inflate(R.layout.generic_refreshable_list_page, null, false);
        findSwipeRefreshLayout();
        findRecyclerView();
        findEmptyView();

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
//        findSwipeRefreshLayout();
//        findRecyclerView();
        refreshOptionsButton();
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
