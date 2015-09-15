package com.maninbrown.ucladining.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.maninbrown.ucladining.R;
import com.maninbrown.ucladining.util.TypefaceUtil;

import java.util.ArrayList;

import api.DiningAPI;
import models.BaseModel;
import models.RateableItem;
import models.Section;
import models.SectionItem;
import util.diningAPICallbacks.OnCompleteListener;
import util.diningAPICallbacks.OnFailureListener;
import util.diningAPICallbacks.OnSuccessListener;

/**
 * Created by Rahul on 9/15/2015.
 */
public class QuickServiceRestaurantsListPage extends BaseFragment {
    protected static final String TAG = "QuickServicesPage";

    private boolean isRefreshing = false;

    private Section mSection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRefreshButtonIsOn(true);
        setBackButtonOn(true);
        setToolbarTitle("Quick Services");
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
            DiningAPI.getQuickServiceRestaurantsPage(new OnCompleteListener() {
                @Override
                public void onComplete() {
                    isRefreshing = false;
                    hideSwipeRefresh();
                }
            }, new OnSuccessListener() {
                @Override
                public void onSuccess(BaseModel baseModel) {
                    if (baseModel == null) {
                        Log.e(TAG, "onSuccess has null base model");
                        setRecyclerAdapter(null);
                    } else {
                        mSection = (Section) baseModel;
                        parseAndPopulateList();
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure() {
                    setRecyclerAdapter(null);
                }
            });
        }
    }

    private void parseAndPopulateList() {
        ArrayList<SectionItem> sectionItems = mSection.getSectionItems();
        setRecyclerAdapter((sectionItems == null) ? null : new QuickServiceRestaurantsPageAdapter(sectionItems));
    }

    public static class QuickServiceRestaurantsViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView textView;

        public QuickServiceRestaurantsViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.quick_service_item_card);
            textView = (TextView) itemView.findViewById(R.id.quick_service_item_text);
        }
    }

    public class QuickServiceRestaurantsPageAdapter extends RecyclerView.Adapter<QuickServiceRestaurantsViewHolder> {
        private ArrayList<SectionItem> sectionItems;

        public QuickServiceRestaurantsPageAdapter(ArrayList<SectionItem> items) {
            sectionItems = items;
        }

        @Override
        public QuickServiceRestaurantsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return null;
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.quick_service_restaurants_layout, parent, false);
            return new QuickServiceRestaurantsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(QuickServiceRestaurantsViewHolder holder, int position) {
            SectionItem sectionItem = sectionItems.get(position);
            if (sectionItem instanceof RateableItem) {
                final RateableItem rateableItem = (RateableItem) sectionItem;
                holder.textView.setText(rateableItem.getItemName());
                holder.textView.setTypeface(TypefaceUtil.getBold(getActivity()));

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), rateableItem.getItemName() + " clicked", Toast.LENGTH_SHORT).show();
                        // TODO: go to correct quick service menu page
                    }
                });
            } else {
                Log.e(TAG, "onBindViewHolder is not a RateableItem");
            }
        }

        @Override
        public int getItemCount() {
            return (sectionItems == null) ? 0 : sectionItems.size();
        }
    }
}
