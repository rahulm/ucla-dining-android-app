package com.maninbrown.ucladining.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maninbrown.ucladining.R;
import com.maninbrown.ucladining.util.TypefaceUtil;

import java.util.ArrayList;

import api.DiningAPI;
import models.BaseModel;
import models.RestaurantHoursList;
import models.RestaurantHoursModel;
import util.diningAPICallbacks.OnCompleteListener;
import util.diningAPICallbacks.OnFailureListener;
import util.diningAPICallbacks.OnSuccessListener;

/**
 * Created by Rahul on 9/15/2015.
 */
public class RestaurantHoursPage extends BaseFragment {
    protected static final String TAG = "RestaurantHoursPage";

    private boolean isRefreshing = false;

    private RestaurantHoursList mRestaurantHoursList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackButtonOn(true);
        setRefreshButtonIsOn(true);
        setLayoutId(R.layout.generic_refreshable_list_page);
        setToolbarTitle("Hours");

        // TODO: set options
    }

    @Override
    protected void populateRootView() {
        doRefresh(null);
    }

    @Override
    public void doRefresh(@Nullable RefreshListener refreshListener) {
        if (!isRefreshing) {
            isRefreshing = true;
            showSwipeRefresh();
            DiningAPI.getRestaurantHours(new OnCompleteListener() {
                @Override
                public void onComplete() {
                    isRefreshing = false;
                    hideSwipeRefresh();
                }
            }, new OnSuccessListener() {
                @Override
                public void onSuccess(BaseModel baseModel) {
                    if (baseModel != null && baseModel instanceof RestaurantHoursList) {
                        mRestaurantHoursList = (RestaurantHoursList) baseModel;
                        parseAndPopulateList();
                    } else {
                        setRecyclerAdapter(null);
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure() {
                    Log.e(TAG, "onFailure reached");
                    setRecyclerAdapter(null);
                }
            });
        }
    }

    private void parseAndPopulateList() {
        ArrayList<RestaurantHoursModel> hoursModels = mRestaurantHoursList.getRestaurantHoursModels();
        if (hoursModels != null && !hoursModels.isEmpty()) {
            setRecyclerAdapter(new RestaurantHoursAdapter(hoursModels));
        } else {
            setRecyclerAdapter(null);
        }
    }


    public static class RestaurantHoursHolder extends RecyclerView.ViewHolder {
        public TextView restaurantName;

        public CardView menuCard;
        public LinearLayout menuList;

        public RestaurantHoursHolder(View view) {
            super(view);
            restaurantName = (TextView) view.findViewById(R.id.restaurant_hours_name_text);
            menuCard = (CardView) view.findViewById(R.id.restaurant_hours_list_card);
            menuList = (LinearLayout) view.findViewById(R.id.restaurant_hours_list_layout);
        }
    }

    public class RestaurantHoursAdapter extends RecyclerView.Adapter<RestaurantHoursHolder> {

        private ArrayList<RestaurantHoursModel> restaurantHoursModels;

        public RestaurantHoursAdapter(ArrayList<RestaurantHoursModel> models) {
            restaurantHoursModels = models;
        }

        @Override
        public RestaurantHoursHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return null;
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.restaurant_hours_item_layout, parent, false);
            return new RestaurantHoursHolder(view);
        }

        @Override
        public void onBindViewHolder(RestaurantHoursHolder holder, int position) {
            RestaurantHoursModel model = restaurantHoursModels.get(position);
            TextView titleView = holder.restaurantName;
            titleView.setText(model.getRestaurantName());
            titleView.setTypeface(TypefaceUtil.getBold(getActivity()));

            ArrayList<String> hours = model.getHoursList();
            LinearLayout layout = holder.menuList;
            layout.removeAllViews();
            if (hours!=null && !hours.isEmpty()) {
                for (String hour : hours) {
                    View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.restaurant_hours_item, null, false);
                    TextView textView = (TextView) rootView.findViewById(R.id.restaurant_hours_item_text);
                    textView.setText(hour.replace("<br>", ""));
                    textView.setTypeface(TypefaceUtil.getItalic(getActivity()));

                    ViewParent parent = rootView.getParent();
                    if (parent != null) {
                        ((ViewGroup) parent).removeView(rootView);
                    }
                    layout.addView(rootView);
                }
                holder.menuCard.setVisibility(View.VISIBLE);
            } else {
                holder.menuCard.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return (restaurantHoursModels == null) ? 0 : restaurantHoursModels.size();
        }
    }
}
