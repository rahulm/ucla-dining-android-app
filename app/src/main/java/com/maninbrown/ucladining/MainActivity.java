package com.maninbrown.ucladining;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.maninbrown.ucladining.fragments.BaseFragment;


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

    private void setUpContentFrame() {
        mContentFrame = (FrameLayout) findViewById(R.id.main_content);
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
                mRefreshButton.setClickable(false);
                mRefreshButton.setVisibility(View.INVISIBLE);
            } else {
                mRefreshButton.setVisibility(View.VISIBLE);
                mRefreshButton.setClickable(true);
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
                mBackButton.setClickable(false);
                mBackButton.setVisibility(View.INVISIBLE);
            } else {
                mBackButton.setVisibility(View.VISIBLE);
                mBackButton.setClickable(true);
            }
        }
    }
}
