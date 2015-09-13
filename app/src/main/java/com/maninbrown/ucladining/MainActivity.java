package com.maninbrown.ucladining;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.maninbrown.ucladining.fragments.BaseFragment;
import com.maninbrown.ucladining.fragments.HomeOptionsPage;


/**
 * MainActivity is the main activity of the app.
 * Contains references to the title layout and the slide options layout, among global layouts.
 * <p/>
 * Created by Rahul on 9/12/2015.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Toolbar stuff
    private Toolbar mToolbar;
    private ImageButton mBackButton;
    private ImageButton mRefreshButton;
    private TextView mTitleView;


    // Main content stuff
    private FrameLayout mContentFrame;
    private BaseFragment mCurrFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set main content layout
        setContentView(R.layout.main_activity_layout);

        // Set up Toolbar and action bar layouts etc.
        setUpToolbar();

        // Set up main fragment
        setUpContentFrame();
    }

    @Override
    public void onBackPressed() {
        if (mCurrFragment==null || (mCurrFragment instanceof HomeOptionsPage)) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void setUpContentFrame() {
        mContentFrame = (FrameLayout) findViewById(R.id.main_content);
        setUpFragmentManager();
        showFragment(new HomeOptionsPage());
    }

    private void setUpToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.custom_toolbar);

        setSupportActionBar(mToolbar);

        mBackButton = (ImageButton) mToolbar.findViewById(R.id.custom_toolbar_button_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Back pressed", Toast.LENGTH_SHORT).show();
            }
        });

        mRefreshButton = (ImageButton) mToolbar.findViewById(R.id.custom_toolbar_button_refresh);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Refresh pressed", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Refresh pressed!");
                if (mCurrFragment!=null) mCurrFragment.doRefresh();
            }
        });

        mTitleView = (TextView) mToolbar.findViewById(R.id.custom_toolbar_title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Arvo/Arvo-BoldItalic.ttf");
        mTitleView.setTypeface(typeface);
    }

    /**
     * setUpFragmentManager is used to correctly assign mContent when back stack changes, i.e. when the back button is pressed
     */
    private void setUpFragmentManager() {
        getSupportFragmentManager()
                .addOnBackStackChangedListener(new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_content);
                        if (fragment == null) {
                            Log.e(TAG, "onBackStackChanged fragment found is null!");
                        } else {
                            if (fragment instanceof BaseFragment) {
                                mCurrFragment = (BaseFragment) fragment;
                            } else {
                                Log.e(TAG, "onBackStackChanged found an incorrect fragment!");
                            }
                        }
                    }
                });
    }

    public void showFragment(BaseFragment fragment) {
        if (fragment!=null) {
            mCurrFragment = fragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, mCurrFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }


    // Toolbar controls

    public void setToolbarTitle(String title) {
        if (mTitleView!=null) {
            mTitleView.setText(title);
            mTitleView.setVisibility(View.VISIBLE);
        }
    }

    public void setToolbarTitleVisibility(int visibility) {
        if (mTitleView!=null) {
            try {
                mTitleView.setVisibility(visibility);
            } catch (Exception e) {
                Log.e(TAG, "setToolbarTitleVisibility couldn't be set for visibility: " + visibility);
                e.printStackTrace();
            }
        }
    }

    /**
     * This toggles the refresh button to be visible and clickable if isOn is true, and invisible and not clickable if false.
     *
     * @param isOn      Turns refresh button on if true, and off if false.
     */
    public void toggleRefreshButton(boolean isOn) {
        if (mRefreshButton!=null) {
            if (isOn) {
                mRefreshButton.setVisibility(View.VISIBLE);
                mRefreshButton.setClickable(true);
            } else {
                mRefreshButton.setClickable(false);
                mRefreshButton.setVisibility(View.INVISIBLE);
            }
        }
    }


    /**
     * This toggles the back button to be visible and clickable if isOn is true, and invisible and not clickable if false.
     *
     * @param isOn      Turns back button on if true, and off if false.
     */
    public void toggleBackButton(boolean isOn) {
        if (mBackButton!=null) {
            if (isOn) {
                mBackButton.setVisibility(View.VISIBLE);
                mBackButton.setClickable(true);
            } else {
                mBackButton.setClickable(false);
                mBackButton.setVisibility(View.INVISIBLE);
            }
        }
    }


}
