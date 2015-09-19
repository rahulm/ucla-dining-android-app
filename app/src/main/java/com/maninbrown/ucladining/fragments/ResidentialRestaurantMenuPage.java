package com.maninbrown.ucladining.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maninbrown.ucladining.R;
import com.maninbrown.ucladining.util.FoodItemUtils;
import com.maninbrown.ucladining.util.OnOptionsDismissListener;
import com.maninbrown.ucladining.util.TypefaceUtil;
import com.maninbrown.ucladining.util.bottomSheetUtils.GeneralUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.Collections;
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

    private Section mFullMenuSection;

    private DateTime mCurrentDate;

    private DatePicker mDatePicker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey(DiningAPIEndpoints.PARAM_KEY_RESTAURANT)) {
            Log.e(TAG, "onCreate bad things happened, bundle is not properly formatted, or is null!");
            setRecyclerAdapter(null);
        }
        mRestaurantName = bundle.getString(DiningAPIEndpoints.PARAM_KEY_RESTAURANT);
        addCurrentOption(DiningAPIEndpoints.PARAM_KEY_RESTAURANT, mRestaurantName);

        for (String key : bundle.keySet()) {
            Object object = bundle.get(key);
            if (object instanceof String) {
                addCurrentOption(key, (String) object);
            }
        }

        String keyDay = ResidentialRestaurantsPage.PARAM_DATE_DAY, keyMonth = ResidentialRestaurantsPage.PARAM_DATE_MONTH,
                keyYear = ResidentialRestaurantsPage.PARAM_DATE_YEAR;
        if (bundle.containsKey(keyDay) && bundle.containsKey(keyMonth) && bundle.containsKey(keyYear)) {
            mCurrentDate = new DateTime()
                    .withYear(bundle.getInt(keyYear))
                    .withMonthOfYear(bundle.getInt(keyMonth))
                    .withDayOfMonth(bundle.getInt(keyDay));
        }

        setRefreshButtonIsOn(true);
        setBackButtonOn(true);
        setToolbarTitle(mRestaurantName);
        setLayoutId(R.layout.generic_refreshable_list_page);


        setOptionsButtonIsOn(true, null, new OnOptionsDismissListener() {
            @Override
            public void onOptionsDismiss() {
                if (mDatePicker != null) {
                    logDebug("onOptionsDismiss reached for setting new current date time");
                    mCurrentDate = new DateTime()
                            .withDayOfMonth(mDatePicker.getDayOfMonth())
                            .withMonthOfYear(mDatePicker.getMonth() + 1)
                            .withYear(mDatePicker.getYear());
                }
                doRefresh(null);
            }
        });
    }

    @Override
    protected void populateRootView() {
        doRefresh(null);
    }

    @Override
    protected ArrayList<View> createOptionsLayoutViews() {
//        return super.createOptionsLayoutViews();
        ArrayList<View> views = new ArrayList<>();


        views.add(GeneralUtils.getInflatedBottomSheetTitleView(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOptionsLayout();
            }
        }));

        String[] strings = getResources().getStringArray(R.array.spinner_residential_restaurants);
        ArrayList<String> items = new ArrayList<>();
        Collections.addAll(items, strings);

        String currentMealTime = null;
        HashMap<String, String> options = getCurrentOptions();
        if (options != null && options.containsKey(DiningAPIEndpoints.PARAM_KEY_MEAL_TIME)) {
            currentMealTime = options.get(DiningAPIEndpoints.PARAM_KEY_MEAL_TIME);
        }

        LinearLayout linearLayout = GeneralUtils.getInflatedBottomSheetOptionsPickerLayout(getActivity(),
                items, currentMealTime,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String itemName = parent.getItemAtPosition(position).toString();
                        logDebug("onItemSelected for position: " + position);
                        logDebug("onItemSelected item string is: " + itemName);
                        addCurrentOption(DiningAPIEndpoints.PARAM_KEY_MEAL_TIME, itemName);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        logDebug("onNothingSelected nothing selected");
                        removeCurrentOption(DiningAPIEndpoints.PARAM_KEY_MEAL_TIME);
                    }
                });
        views.add(linearLayout);


        mDatePicker = GeneralUtils.getInflatedBottomSheetDatePicker(getActivity(), mCurrentDate,
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mCurrentDate = new DateTime()
                                .withYear(year)
                                .withMonthOfYear(monthOfYear + 1)
                                .withDayOfMonth(dayOfMonth);
                    }
                });
        views.add(mDatePicker);

        return views;
    }

    @Override
    public void doRefresh(@Nullable RefreshListener refreshListener) {

        if (!isRefreshing) {
            isRefreshing = true;
            showSwipeRefresh();

            HashMap<String, String> map = getCurrentOptions();
            String baseToShow = "Menu for meals", dateToShow = "now", mealTimePrefix = "";
            if (mCurrentDate != null) {
                dateToShow = DateTimeFormat.forPattern("MMM dd, yyyy").print(mCurrentDate);
            }
            if (map != null && map.containsKey(DiningAPIEndpoints.PARAM_KEY_MEAL_TIME)) {
                mealTimePrefix = map.get(DiningAPIEndpoints.PARAM_KEY_MEAL_TIME);
            }
            getMainActivity().showFloatingInfoText(mealTimePrefix + " " + baseToShow + " " + dateToShow);

            addCurrentOption(DiningAPIEndpoints.PARAM_KEY_RESTAURANT, mRestaurantName);

            DiningAPI.getResidentialRestaurantFullMenu(getCurrentOptions(), new OnCompleteListener() {
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
