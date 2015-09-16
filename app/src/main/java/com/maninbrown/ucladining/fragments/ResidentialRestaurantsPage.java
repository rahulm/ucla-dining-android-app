package com.maninbrown.ucladining.fragments;

import android.app.Dialog;
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
import com.maninbrown.ucladining.util.FoodItemUtils;
import com.maninbrown.ucladining.util.OnOptionsDismissListener;
import com.maninbrown.ucladining.util.TypefaceUtil;

import java.util.ArrayList;

import api.DiningAPI;
import api.DiningAPIEndpoints;
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
    protected static final String TAG = "ResRestaurantsPage";

    private boolean isRefreshing = false;

    private SectionList mSectionList;

    @Override
    public void doRefresh(final RefreshListener refreshListener) {
        logDebug("doRefresh reached begin");

        if (!isRefreshing) {
            logDebug("doRefresh attempting refresh");
            isRefreshing = true;

            // TODO: refresh if not already refreshing
            if (!isLayoutRefreshing()) {
                logDebug("doRefresh trying to show refresh icon");
                showSwipeRefresh();
            }
            DiningAPI.getResidentialRestaurantsPage(new OnCompleteListener() {
                @Override
                public void onComplete() {
                    logDebug("onComplete reached");
                    isRefreshing = false;
                    hideSwipeRefresh();
                    if (refreshListener != null) refreshListener.OnRefreshComplete();
                }
            }, new OnSuccessListener() {
                @Override
                public void onSuccess(BaseModel baseModel) {
                    logDebug("onSuccess reached");
                    if (baseModel instanceof SectionList) {
                        mSectionList = (SectionList) baseModel;
                        parseAndPopulateList();
                    } else {
                        Log.e(TAG, "onSuccess BaseModel isn't a SectionList");
                        setRecyclerAdapter(null);
                        hideSwipeRefresh();
                    }
//                    hideSwipeRefresh();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure() {
                    hideSwipeRefresh();
                    Log.e(TAG, "onFailure reached for residential restaurants call");
//                    Toast.makeText(getActivity(), "Uh oh, there was a problem refreshing! Please try again!", Toast.LENGTH_SHORT).show();
                    setRecyclerAdapter(null);
                }
            });
        }
        logDebug("doRefresh reached end");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("Residential Restaurants");
        setRefreshButtonIsOn(true);
        setBackButtonOn(true);

        final OnOptionsDismissListener onOptionsDismissListener = new OnOptionsDismissListener() {
            @Override
            public void onOptionsDismiss() {
                Toast.makeText(getActivity(), "dismissing options", Toast.LENGTH_SHORT).show();
                // TODO: refresh stuff
            }
        };

        // TODO: set up options
        setOptionsButtonIsOn(true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "options clicked", Toast.LENGTH_SHORT).show();
                // TODO testing
                ArrayList<View> views = new ArrayList<View>();
                views.add(LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet_title, null, false));
                showOptionsLayout(views, onOptionsDismissListener);
            }
        });

        setLayoutId(R.layout.generic_refreshable_list_page);
    }

    @Override
    protected void populateRootView() {
        logDebug("populateRootView reached begin");
        doRefresh(null);
//        parseAndPopulateList();
    }

    private void parseAndPopulateList() {
        logDebug("parseAndPopulateList reached begin");
        if (mSectionList == null) {
            Log.e(TAG, "parseAndPopulateList section list is null!");
            setRecyclerAdapter(null);
        } else {
            RecyclerView.Adapter adapter = (mSectionList.getSections() == null) ? null : new ResidentialRestaurantsAdapter(mSectionList.getSections());
            setRecyclerAdapter(adapter);
        }
    }

    private void openFullMenuPage(String restaurant) {
        Bundle bundle = new Bundle();
        bundle.putString(DiningAPIEndpoints.PARAM_KEY_RESTAURANT, restaurant);
        BaseFragment fragment = new ResidentialRestaurantMenuPage();
        fragment.setArguments(bundle);
        getMainActivity().showFragment(fragment);
    }


    public static class RestaurantsPageSectionHolder extends RecyclerView.ViewHolder {
        public CardView restaurantHeaderCard;
        public TextView restaurantName;
        public TextView restaurantFullMenuText;

        public CardView restaurantMenuCard;
        public LinearLayout restaurantMenuList;

        public RestaurantsPageSectionHolder(View item) {
            super(item);
            restaurantHeaderCard = (CardView) item.findViewById(R.id.restaurant_card_restaurant_card);
            restaurantName = (TextView) restaurantHeaderCard.findViewById(R.id.restaurant_card_name);
            restaurantFullMenuText = (TextView) restaurantHeaderCard.findViewById(R.id.restaurant_card_full_menu);

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
//                    Toast.makeText(getActivity(), section.getRestaurantName() + " clicked.", Toast.LENGTH_SHORT).show();
                    // TODO: open next full menu fragment with correct restaurant
                    openFullMenuPage(section.getRestaurantName());
                }
            });
            holder.restaurantHeaderCard.setClickable(true);
            holder.restaurantFullMenuText.setTypeface(TypefaceUtil.getItalic(getActivity()));

            ArrayList<SectionItem> sectionItems = section.getSectionItems();
            LinearLayout layout = holder.restaurantMenuList;
            if (layout == null) {
                Log.e(TAG, "onBindViewHolder linear layout is null");
            }
            layout.setVisibility(View.VISIBLE);
            layout.removeAllViews();
            layout.removeAllViewsInLayout();

            if (sectionItems == null || sectionItems.isEmpty()) {
                holder.restaurantMenuCard.setVisibility(View.GONE);
            } else {
                holder.restaurantMenuCard.setVisibility(View.VISIBLE);
                for (SectionItem item : sectionItems) {
                    if (item instanceof RateableItem) {
                        final RateableItem rateableItem = (RateableItem) item;
                        final View rootView = getActivity().getLayoutInflater().inflate(R.layout.food_item_card_layout, null, false);
                        rootView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Toast.makeText(getActivity(), rateableItem.getItemName() + " clicked.", Toast.LENGTH_SHORT).show();
                                //  open the right nutrition pop up
                                rootView.setClickable(false);
                                showSwipeRefresh();
                                FoodItemUtils.openInfoPopupForFoodItem(rateableItem, getActivity(), new OnCompleteListener() {
                                    @Override
                                    public void onComplete() {
                                        rootView.setClickable(true);
                                        hideSwipeRefresh();
                                    }
                                },null ,null);
                            }
                        });
                        rootView.setClickable(true);

                        TextView titleText = ((TextView) rootView.findViewById(R.id.food_item_name));
                        titleText.setText(rateableItem.getItemName());
                        titleText.setTypeface(TypefaceUtil.getRegular(getActivity()));

                        TextView subText = (TextView) rootView.findViewById(R.id.food_item_subtitle);
                        String details = rateableItem.getItemDescription();
                        if (details == null || details.isEmpty()) {
                            subText.setText("");
                            subText.setVisibility(View.GONE);
                        } else {
                            subText.setVisibility(View.VISIBLE);
                            subText.setText(details);
                            subText.setTypeface(TypefaceUtil.getItalic(getActivity()));
                        }

                        ViewParent parent = rootView.getParent();
                        if (parent != null) {
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
}
