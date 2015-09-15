package com.maninbrown.ucladining.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maninbrown.ucladining.R;

import java.util.ArrayList;

import api.DiningAPI;
import models.DiningServiceModel;

/**
 * Created by Rahul on 9/13/2015.
 */
public class HomeOptionsPage extends BaseFragment {

    protected static final String TAG = "HomeOptionsPage";

    private static Typeface mTypeface;

    private static ArrayList<DiningServiceModel> mDiningServiceModels;

    @Override
    public void doRefresh(RefreshListener refreshListener) {
        // TODO:nothing
        hideSwipeRefresh();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("UCLA Dining");
        setBackButtonOn(false);
        setRefreshButtonIsOn(false);
        setOptionsButtonIsOn(false, null);
        setLayoutId(R.layout.generic_refreshable_list_page);
        setUpOptionsModels();

        // TODO: might want to do something if this fails
        DiningAPI.refreshDiningHomePageServer(null, null, null);
    }

    private void setUpOptionsModels() {
        mDiningServiceModels = new ArrayList<>(3);
        mDiningServiceModels.add(new DiningServiceModel("Residential Restaurants", DiningServiceModel.DiningServiceType.DiningMenu));
        mDiningServiceModels.add(new DiningServiceModel("Quick Service Restaurants", DiningServiceModel.DiningServiceType.DiningMenu));
        mDiningServiceModels.add(new DiningServiceModel("Restaurant Hours", DiningServiceModel.DiningServiceType.DiningHours));
    }

    @Override
    protected void populateRootView() {
        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Arvo/Arvo-Bold.ttf");
        }
        if (mDiningServiceModels==null || mDiningServiceModels.isEmpty()) {
//            Toast.makeText(getActivity(), "Dining Service models list is null!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "populateRootView dining service models list is null");
        }
        setRecyclerAdapter(new OptionsRecyclerAdapter(mDiningServiceModels));

    }

    public static class OptionViewHolder extends RecyclerView.ViewHolder {
        TextView serviceText;
        CardView cardView;

        public OptionViewHolder(View itemView) {
            super(itemView);
            serviceText = (TextView) itemView.findViewById(R.id.home_option_item_text);
            cardView = (CardView) itemView.findViewById(R.id.home_option_item_cardview);
        }
    }

    public class OptionsRecyclerAdapter extends RecyclerView.Adapter<OptionViewHolder> {
        private ArrayList<DiningServiceModel> mDiningModels;

        public OptionsRecyclerAdapter(ArrayList<DiningServiceModel> models) {
            mDiningModels = models;
        }

        @Override
        public OptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return null;
            Log.d(TAG, "onCreateViewHolder reached begin");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_options_item_layout, parent, false);
            OptionViewHolder optionViewHolder = new OptionViewHolder(view);
            return optionViewHolder;
        }

        @Override
        public void onBindViewHolder(OptionViewHolder holder, final int position) {
            final String name = mDiningModels.get(position).getServiceName();
            TextView textView = holder.serviceText;
            textView.setText(name);
            if (!textView.getTypeface().equals(mTypeface)) {
                textView.setTypeface(mTypeface);
            }

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getActivity(), "card view clicked for: " + name, Toast.LENGTH_SHORT).show();
                    // open next fragment as appropriate
                    switch (position) {
                        case 0:
                            getMainActivity().showFragment(new ResidentialRestaurantsPage());
                            break;
                        case 1:
                            getMainActivity().showFragment(new QuickServiceRestaurantsListPage());
                            break;
                        case 2:
                            getMainActivity().showFragment(new RestaurantHoursPage());
                            break;
                    }
                }
            });
            holder.cardView.setClickable(true);
        }

        @Override
        public int getItemCount() {
            return (mDiningModels == null) ? 0 : mDiningModels.size();
        }
    }
}
