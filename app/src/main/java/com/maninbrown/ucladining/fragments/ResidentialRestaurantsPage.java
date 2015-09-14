package com.maninbrown.ucladining.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
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
import models.SectionList;
import util.diningAPICallbacks.OnCompleteListener;
import util.diningAPICallbacks.OnFailureListener;
import util.diningAPICallbacks.OnSuccessListener;

/**
 * Created by Rahul on 9/13/2015.
 */
public class ResidentialRestaurantsPage extends BaseFragment {
    private static final String TAG = "ResRestaurantsPage";

    private boolean isRefreshing = false;

    private SectionList mSectionList;

    @Override
    public void doRefresh(final RefreshListener refreshListener) {
        // TODO: refresh if not already refreshing
        if (!isLayoutRefreshing()) {
            Log.d(TAG, "doRefresh trying to show refresh");
            showSwipeRefresh();
        }

        if (!isRefreshing) {
            isRefreshing = true;
            DiningAPI.getResidentialRestaurantsPage(new OnCompleteListener() {
                @Override
                public void onComplete() {
                    hideSwipeRefresh();
                    isRefreshing = false;
                    if (refreshListener != null) refreshListener.OnRefreshComplete();
                }
            }, new OnSuccessListener() {
                @Override
                public void onSuccess(BaseModel baseModel) {
                    if (baseModel instanceof SectionList) {
                        mSectionList = (SectionList) baseModel;
                        parseAndPopulateList();
                    } else {
                        Log.e(TAG, "onSuccess BaseModel isn't a SectionList");
                        setRecyclerAdapter(null);
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure() {
                    Log.e(TAG, "onFailure reached for residential restaurants call");
                    Toast.makeText(getActivity(), "Uh oh, there was a problem refreshing! Please try again!", Toast.LENGTH_SHORT).show();
                    setRecyclerAdapter(null);
                }
            });
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("Residential Restaurants");
        setRefreshButtonIsOn(true);
        setBackButtonOn(true);
//        setOptionsButtonIsOn(true, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Option button pressed", Toast.LENGTH_SHORT).show();
//            }
//        });

        setLayoutId(R.layout.generic_refreshable_list_page);
    }

    @Override
    protected void populateRootView() {
        // TODO:
        doRefresh(null);
//        parseAndPopulateList();
    }

    private void parseAndPopulateList() {
        if (mSectionList == null) {
            Log.e(TAG, "parseAndPopulateList section list is null!");
            setRecyclerAdapter(null);
        } else {
            RecyclerView.Adapter adapter = (mSectionList.getSections() == null) ? null : new ResidentialRestaurantsAdapter(mSectionList.getSections());
            setRecyclerAdapter(adapter);
        }
    }


    public static class RestaurantsPageSectionHolder extends RecyclerView.ViewHolder {
        public CardView restaurantHeaderCard;
        public TextView restaurantName;

        public CardView restaurantMenuCard;
        public LinearLayout restaurantMenuList;

        public RestaurantsPageSectionHolder(View item) {
            super(item);
            restaurantHeaderCard = (CardView) item.findViewById(R.id.restaurant_card_restaurant_card);
            restaurantName = (TextView) restaurantHeaderCard.findViewById(R.id.restaurant_card_name);

            restaurantMenuCard = (CardView) item.findViewById(R.id.restaurant_card_menu_card);
            restaurantMenuList = (LinearLayout) restaurantMenuCard.findViewById(R.id.restaurant_card_menu_list);
        }
    }

    public class ResidentialRestaurantsAdapter extends RecyclerView.Adapter<RestaurantsPageSectionHolder> {
        private ArrayList<Section> mSections;

        public ResidentialRestaurantsAdapter(ArrayList<Section> sections) {
            mSections = sections;
        }

        @Override
        public RestaurantsPageSectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return null;
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item_card_layout, parent, false);
            return new RestaurantsPageSectionHolder(view);
        }

        @Override
        public void onBindViewHolder(RestaurantsPageSectionHolder holder, int position) {
            final Section section = mSections.get(position);
            holder.restaurantName.setText(section.getRestaurantName());
            holder.restaurantName.setTypeface(TypefaceUtil.getBold(getActivity()));
            holder.restaurantHeaderCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), section.getRestaurantName() + " clicked.", Toast.LENGTH_SHORT).show();
                    // TODO: open next full menu fragment with correct restaurant
                }
            });
            holder.restaurantHeaderCard.setClickable(true);

            ArrayList<SectionItem> sectionItems = section.getSectionItems();
            LinearLayout layout = holder.restaurantMenuList;
            if (layout==null) {
                Log.e(TAG, "onBindViewHolder linear layout is null");
            }
            layout.setVisibility(View.VISIBLE);
            layout.removeAllViews();
            layout.removeAllViewsInLayout();

            if (sectionItems==null || sectionItems.isEmpty()) {
                holder.restaurantMenuCard.setVisibility(View.GONE);
            } else {
                holder.restaurantMenuCard.setVisibility(View.VISIBLE);
                for (SectionItem item : sectionItems) {
                    if (item instanceof RateableItem) {
                        final RateableItem rateableItem = (RateableItem) item;
                        View rootView = getActivity().getLayoutInflater().inflate(R.layout.food_item_card_layout, null, false);
                        rootView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity(), rateableItem.getItemName() + " clicked.", Toast.LENGTH_SHORT).show();
                                // TODO: open the right nutrition pop up
                            }
                        }); rootView.setClickable(true);

                        TextView titleText = ((TextView) rootView.findViewById(R.id.food_item_name));
                        titleText.setText(rateableItem.getItemName());
                        titleText.setTypeface(TypefaceUtil.getRegular(getActivity()));

                        TextView subText = (TextView) rootView.findViewById(R.id.food_item_subtitle);
                        String details = rateableItem.getItemDescription();
                        if (details==null || details.isEmpty()) {
                            subText.setText(""); subText.setVisibility(View.GONE);
                        } else {
                            subText.setVisibility(View.VISIBLE); subText.setText(details);
                            subText.setTypeface(TypefaceUtil.getItalic(getActivity()));
                        }

                        ViewParent parent = rootView.getParent();
                        if (parent!=null) {
                            ((ViewGroup) parent).removeView(rootView);
                        }
                        layout.addView(rootView);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return (mSections == null) ? 0 : mSections.size();
        }
    }


    //    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        //return super.onCreateView(inflater, container, savedInstanceState);
//
//        SwipeRefreshLayout layout = (SwipeRefreshLayout) inflater.inflate(R.layout.res_restaurants_list_page, null, false);
//        setRootView(layout);
//
//
//
//        return getRootView();
//    }
}
