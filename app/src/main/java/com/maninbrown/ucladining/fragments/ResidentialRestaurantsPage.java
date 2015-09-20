package com.maninbrown.ucladining.fragments;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maninbrown.ucladining.R;
import com.maninbrown.ucladining.util.DateUtils;
import com.maninbrown.ucladining.util.FoodItemUtils;
import com.maninbrown.ucladining.util.OnOptionsDismissListener;
import com.maninbrown.ucladining.util.TypefaceUtil;
import com.maninbrown.ucladining.util.bottomSheetUtils.GeneralUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.HashMap;

import api.DiningAPI;
import api.DiningAPIEndpoints;
import models.BaseModel;
import models.RateableItem;
import models.Section;
import models.SectionItem;
import models.SectionListWithOptions;
import util.diningAPICallbacks.OnCompleteListener;
import util.diningAPICallbacks.OnFailureListener;
import util.diningAPICallbacks.OnSuccessListener;

/**
 * Created by Rahul on 9/13/2015.
 */
public class ResidentialRestaurantsPage extends BaseFragment {
    protected static final String TAG = "ResRestaurantsPage";

    private boolean isRefreshing = false;

    private SectionListWithOptions mSectionList;

    private DateTime mCurrentDate;

    private DatePicker mDatePicker;

    private ArrayList<String> mOptions;


    @Override
    public void doRefresh(final RefreshListener refreshListener) {
        logDebug("doRefresh reached begin");

        if (!isRefreshing) {
            logDebug("doRefresh attempting refresh");
            isRefreshing = true;

            if (!isLayoutRefreshing()) {
                logDebug("doRefresh trying to show refresh icon");
                showSwipeRefresh();
            }

            if (mCurrentDate != null) {
                addCurrentOption(DiningAPIEndpoints.PARAM_KEY_DATE, DateUtils.getDateStringFromDateTime(mCurrentDate));
                logDebug("doRefresh date param: " + DiningAPIEndpoints.PARAM_KEY_DATE + ": " + DateUtils.getDateStringFromDateTime(mCurrentDate));
            }

            HashMap<String, String> map = getCurrentOptions();
            String baseToShow = "Menu for meals", dateToShow = "now", mealTimePrefix = "";
            if (mCurrentDate != null) {
                dateToShow = DateTimeFormat.forPattern("MMM dd, yyyy").print(mCurrentDate);
            }
            if (map != null && map.containsKey(DiningAPIEndpoints.PARAM_KEY_MEAL_TIME)) {
                mealTimePrefix = map.get(DiningAPIEndpoints.PARAM_KEY_MEAL_TIME);
            }
            getMainActivity().showFloatingInfoText(mealTimePrefix + " " + baseToShow + " " + dateToShow);

            DiningAPI.getResidentialRestaurantsPage(getCurrentOptions(), new OnCompleteListener() {
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
                    if (baseModel instanceof SectionListWithOptions) {
                        mSectionList = (SectionListWithOptions) baseModel;
                        parseAndPopulateList();
                    } else {
                        Log.e(TAG, "onSuccess BaseModel isn't a SectionList");
                        setRecyclerAdapter(null);
                        hideSwipeRefresh();
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure() {
                    hideSwipeRefresh();
                    Log.e(TAG, "onFailure reached for residential restaurants call");
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
                if (mDatePicker != null) {
                    logDebug("onOptionsDismiss reached for setting new current date time");
                    mCurrentDate = new DateTime()
                            .withDayOfMonth(mDatePicker.getDayOfMonth())
                            .withMonthOfYear(mDatePicker.getMonth() + 1)
                            .withYear(mDatePicker.getYear());
                }
                doRefresh(null);
            }
        };


//        setOptionsButtonIsOn(true, null, onOptionsDismissListener);
        setOptionsButtonIsOn(false, null, null);

        setLayoutId(R.layout.generic_refreshable_list_page);
    }

    @Override
    protected ArrayList<View> createOptionsLayoutViews() {
        ArrayList<View> views = new ArrayList<>();


        views.add(GeneralUtils.getInflatedBottomSheetTitleView(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOptionsLayout();
            }
        }));

//        String[] strings = getResources().getStringArray(R.array.spinner_residential_restaurants);
//        ArrayList<String> items = new ArrayList<>();
//        Collections.addAll(items, strings);

        String currentMealTime = null;
        HashMap<String, String> options = getCurrentOptions();
        if (options != null && options.containsKey(DiningAPIEndpoints.PARAM_KEY_MEAL_TIME)) {
            currentMealTime = options.get(DiningAPIEndpoints.PARAM_KEY_MEAL_TIME);
        }

        LinearLayout linearLayout = GeneralUtils.getInflatedBottomSheetOptionsPickerLayout(getActivity(),
                mOptions, currentMealTime,
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
    protected void populateRootView() {
        logDebug("populateRootView reached begin");
        doRefresh(null);
    }

    private void parseAndPopulateList() {
        logDebug("parseAndPopulateList reached begin");
        String currentOption = mSectionList.getCurrentOption();
        ArrayList<String> map = mSectionList.getOptions();
        if (currentOption != null && !currentOption.isEmpty() && map != null && !map.isEmpty()) {
            addCurrentOption(DiningAPIEndpoints.PARAM_KEY_MEAL_TIME, currentOption);
            mOptions = map;

            HashMap<String, String> currentOptions = getCurrentOptions();
            String baseToShow = "Menu for meals", dateToShow = "now", mealTimePrefix = "";
            if (mCurrentDate != null) {
                dateToShow = DateTimeFormat.forPattern("MMM dd, yyyy").print(mCurrentDate);
            }
            if (currentOptions != null && currentOptions.containsKey(DiningAPIEndpoints.PARAM_KEY_MEAL_TIME)) {
                mealTimePrefix = currentOptions.get(DiningAPIEndpoints.PARAM_KEY_MEAL_TIME);
            }
            getMainActivity().showFloatingInfoText(mealTimePrefix + " " + baseToShow + " " + dateToShow);

            setOptionsButtonIsOn(true, null, new OnOptionsDismissListener() {
                @Override
                public void onOptionsDismiss() {
                    doRefresh(null);
                }
            });
        } else {
            removeCurrentOption(DiningAPIEndpoints.PARAM_KEY_MEAL_TIME);
            mOptions = null;
            getMainActivity().hideFloatingInfoText();
            setOptionsButtonIsOn(false, null, null);
        }

        if (mSectionList == null) {
            Log.e(TAG, "parseAndPopulateList section list is null!");
            setRecyclerAdapter(null);
        } else {
            RecyclerView.Adapter adapter = (mSectionList.getSections() == null) ? null : new ResidentialRestaurantsAdapter(mSectionList.getSections());
            setRecyclerAdapter(adapter);
        }
    }

    public static final String PARAM_DATE_DAY = "day", PARAM_DATE_MONTH = "month", PARAM_DATE_YEAR = "year";

    private void openFullMenuPage(String restaurant) {
        if (restaurant == null || restaurant.isEmpty()) {
            Log.e(TAG, "openFullMenuPage restaurant is not properly formatted.");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(DiningAPIEndpoints.PARAM_KEY_RESTAURANT, restaurant);
        HashMap<String, String> map = getCurrentOptions();
        if (map != null) {
            for (String key : map.keySet()) {
                bundle.putString(key, map.get(key));
            }
        }
        if (mCurrentDate != null) {
            bundle.putInt(PARAM_DATE_DAY, mCurrentDate.getDayOfMonth());
            bundle.putInt(PARAM_DATE_MONTH, mCurrentDate.getMonthOfYear());
            bundle.putInt(PARAM_DATE_YEAR, mCurrentDate.getYear());
        }
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
                                }, null, null);
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
