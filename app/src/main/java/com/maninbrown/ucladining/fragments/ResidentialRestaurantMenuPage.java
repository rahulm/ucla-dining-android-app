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
import com.maninbrown.ucladining.util.FoodItemUtils;
import com.maninbrown.ucladining.util.TypefaceUtil;

import java.util.ArrayList;
import java.util.HashMap;

import api.DiningAPI;
import api.DiningAPIEndpoints;
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
public class ResidentialRestaurantMenuPage extends BaseFragment {
    protected static final String TAG = "ResRestaurantMenu";

    private boolean isRefreshing = false;

    private String mRestaurantName;

    private HashMap<String, String> mOptionsMap;

    private Section mFullMenuSection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey(DiningAPIEndpoints.PARAM_KEY_RESTAURANT)) {
            Log.e(TAG, "onCreate bad things happened, bundle is not properly formmatted, or is null!");
            setRecyclerAdapter(null);
        }
        mRestaurantName = bundle.getString(DiningAPIEndpoints.PARAM_KEY_RESTAURANT);
        mOptionsMap = new HashMap<>();
        mOptionsMap.put(DiningAPIEndpoints.PARAM_KEY_RESTAURANT, mRestaurantName);

        setRefreshButtonIsOn(true);
        setBackButtonOn(true);
        setToolbarTitle(mRestaurantName);
        setLayoutId(R.layout.generic_refreshable_list_page);

        // TODO: set up options
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

            // TODO: add options down the line

            if (mOptionsMap == null || mOptionsMap.isEmpty()) {
                mOptionsMap = new HashMap<>();
                mOptionsMap.put(DiningAPIEndpoints.PARAM_KEY_RESTAURANT, mRestaurantName);
            }

            DiningAPI.getResidentialRestaurantFullMenu(mOptionsMap, new OnCompleteListener() {
                @Override
                public void onComplete() {
                    isRefreshing = false;
                    hideSwipeRefresh();
                }
            }, new OnSuccessListener() {
                @Override
                public void onSuccess(BaseModel baseModel) {
                    if (baseModel == null) {
                        setRecyclerAdapter(null);
                    } else {
                        mFullMenuSection = (Section) baseModel;
                        parseAndPopulateList();
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure() {
                    Log.e(TAG, "onFailure reached!");
                    setRecyclerAdapter(null);
                }
            });
        }
    }

    private void parseAndPopulateList() {
        ArrayList<SectionItem> sectionItems = mFullMenuSection.getSectionItems();
        // TODO: set adapter
        setRecyclerAdapter((sectionItems == null) ? null : new ResidentialRestaurantMenuAdapter(sectionItems));
    }

    public static class ResidentialRestaurantMenuViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView titleTextView;
        public TextView subtitleTextView;

        public ResidentialRestaurantMenuViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.food_item_card);
            titleTextView = (TextView) itemView.findViewById(R.id.food_item_name);
            subtitleTextView = (TextView) itemView.findViewById(R.id.food_item_subtitle);
        }
    }

    public class ResidentialRestaurantMenuAdapter extends RecyclerView.Adapter<ResidentialRestaurantMenuViewHolder> {
        private ArrayList<SectionItem> sectionItems;

        public ResidentialRestaurantMenuAdapter(ArrayList<SectionItem> items) {
            sectionItems = items;
        }

        @Override
        public ResidentialRestaurantMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return null;
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.food_item_card_layout, parent, false);
            return new ResidentialRestaurantMenuViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ResidentialRestaurantMenuViewHolder holder, int position) {
            SectionItem sectionItem = sectionItems.get(position);
            if (sectionItem instanceof RateableItem) {
                final RateableItem rateableItem = (RateableItem) sectionItem;

                holder.cardView.setVisibility(View.VISIBLE);
                holder.cardView.setClickable(true);
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(getActivity(), "food item clicked: " + rateableItem.getItemName(), Toast.LENGTH_SHORT).show();
                        // open the correct nutrition info pop up
                        showSwipeRefresh();
                        FoodItemUtils.openInfoPopupForFoodItem(rateableItem, getActivity(), new OnCompleteListener() {
                            @Override
                            public void onComplete() {
                                hideSwipeRefresh();
                            }
                        }, null, null);
                    }
                });


                TextView textView;
                textView = holder.titleTextView;
                textView.setText(rateableItem.getItemName());
                textView.setTypeface(TypefaceUtil.getBold(getActivity()));

                textView = holder.subtitleTextView;
                String description = rateableItem.getItemDescription();
                if (description == null || description.isEmpty()) {
                    textView.setText("");
                    textView.setVisibility(View.GONE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(description);
                    textView.setTypeface(TypefaceUtil.getItalic(getActivity()));
                }
            } else {
                Log.e(TAG, "onBindViewHolder section item isn't a rateable item!");
                holder.cardView.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return (sectionItems == null) ? 0 : sectionItems.size();
        }
    }
}
