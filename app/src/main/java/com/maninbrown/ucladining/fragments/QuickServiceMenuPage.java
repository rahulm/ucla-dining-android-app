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
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maninbrown.ucladining.R;
import com.maninbrown.ucladining.util.FoodItemUtils;
import com.maninbrown.ucladining.util.OnOptionsDismissListener;
import com.maninbrown.ucladining.util.TypefaceUtil;
import com.maninbrown.ucladining.util.bottomSheetUtils.GeneralUtils;

import java.util.ArrayList;
import java.util.HashMap;

import api.DiningAPI;
import api.DiningAPIEndpoints;
import models.BaseModel;
import models.QuickServiceRestaurantMenu;
import models.RateableItem;
import models.Section;
import models.SectionInfoItem;
import models.SectionItem;
import util.diningAPICallbacks.OnCompleteListener;
import util.diningAPICallbacks.OnFailureListener;
import util.diningAPICallbacks.OnSuccessListener;

/**
 * Created by Rahul on 9/15/2015.
 */
public class QuickServiceMenuPage extends BaseFragment {
    protected static final String TAG = "QuickServiceMenu";

    private boolean isRefreshing = false;

    private RateableItem mRateableItem;

    private QuickServiceRestaurantMenu mQuickServiceRestaurantMenu;

    private String mCurrentOption;

    private HashMap<String, String> mOptions;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBackButtonOn(true);
        setRefreshButtonIsOn(true);
        setLayoutId(R.layout.generic_refreshable_list_page);

        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey(DiningAPIEndpoints.PARAM_KEY_RESTAURANT)) {
            setRecyclerAdapter(null);
            return;
        } else {
            mRateableItem = (RateableItem) bundle.getSerializable(DiningAPIEndpoints.PARAM_KEY_RESTAURANT);
        }

        setToolbarTitle(mRateableItem.getRestaurantName());


        setOptionsButtonIsOn(false, null, null);
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

            if (mCurrentOption != null && !mCurrentOption.isEmpty() && mOptions != null && !mOptions.isEmpty()) {
                HashMap<String, String> map = new HashMap<>();
                map.put(RateableItem.KEY_ITEM_NAME, mRateableItem.getItemName());
                map.put(RateableItem.KEY_ITEM_DESCRIPTION, mRateableItem.getItemDescription());
                map.put(RateableItem.KEY_ITEM_RESTAURANT_NAME, mRateableItem.getRestaurantName());
                map.put(RateableItem.KEY_ITEM_SECTION_NAME, mRateableItem.getSectionName());
                map.put(RateableItem.KEY_TARGET_URL, mOptions.get(mCurrentOption));
                mRateableItem = new RateableItem(map, RateableItem.ItemType.QuickServiceRestaurant);
            }

            DiningAPI.getQuickServiceRestaurantMenu(mRateableItem, new OnCompleteListener() {
                @Override
                public void onComplete() {
                    isRefreshing = false;
                    hideSwipeRefresh();
                }
            }, new OnSuccessListener() {
                @Override
                public void onSuccess(BaseModel baseModel) {
                    if (baseModel != null && baseModel instanceof QuickServiceRestaurantMenu) {
                        mQuickServiceRestaurantMenu = (QuickServiceRestaurantMenu) baseModel;
                        parseAndPopulateList();
                    } else {
                        Log.e(TAG, "onSuccess base model is null or not a quick service restaurant menu");
                        setRecyclerAdapter(null);
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure() {
                    Log.e(TAG, "onFailure reached");
                    setRecyclerAdapter(null);
                    hideSwipeRefresh();
                }
            });
        }
    }

    @Override
    protected ArrayList<View> createOptionsLayoutViews() {
        if (mCurrentOption == null || mCurrentOption.isEmpty() || mOptions == null || mOptions.isEmpty()) {
            Log.e(TAG, "createOptionsLayoutViews has reached null options!");
            return new ArrayList<>();
        }

        ArrayList<View> views = new ArrayList<>();

        views.add(GeneralUtils.getInflatedBottomSheetTitleView(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOptionsLayout();
            }
        }));

        ArrayList<String> options = new ArrayList<>();
        for (String key : mOptions.keySet()) {
            options.add(key);
        }

        final String previousOption = mCurrentOption;
        views.add(GeneralUtils.getInflatedBottomSheetOptionsPickerLayout(getActivity(), options, mCurrentOption,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mCurrentOption = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        mCurrentOption = previousOption;
                    }
                }));

        return views;
    }

    private void parseAndPopulateList() {
        String currentOption = mQuickServiceRestaurantMenu.getCurrentOption();
        HashMap<String, String> map = mQuickServiceRestaurantMenu.getOptions();
        if (currentOption != null && !currentOption.isEmpty() && map != null && !map.isEmpty()) {
            mCurrentOption = currentOption;
            mOptions = map;
            getMainActivity().showFloatingInfoText(mCurrentOption);
            setOptionsButtonIsOn(true, null, new OnOptionsDismissListener() {
                @Override
                public void onOptionsDismiss() {
                    doRefresh(null);
                }
            });
        } else {
            mCurrentOption = null;
            mOptions = null;
            getMainActivity().hideFloatingInfoText();
            setOptionsButtonIsOn(false, null, null);
        }

        ArrayList<Section> sections = mQuickServiceRestaurantMenu.getSections();
        setRecyclerAdapter((sections == null) ? null : new QuickServiceMenuAdapter(sections));
    }


    public static class QuickServiceMenuHolder extends RecyclerView.ViewHolder {
        public CardView sectionHeaderCard;
        public TextView sectionHeaderName;

        public CardView sectionMenuCard;
        public LinearLayout sectionMenuList;

        public QuickServiceMenuHolder(View item) {
            super(item);
            sectionHeaderCard = (CardView) item.findViewById(R.id.quick_service_menu_header_card);
            sectionHeaderName = (TextView) sectionHeaderCard.findViewById(R.id.quick_service_menu_header_text);

            sectionMenuCard = (CardView) item.findViewById(R.id.quick_service_menu_card);
            sectionMenuList = (LinearLayout) sectionMenuCard.findViewById(R.id.quick_service_menu_list);
        }
    }

    public class QuickServiceMenuAdapter extends RecyclerView.Adapter<QuickServiceMenuHolder> {
        private ArrayList<Section> mSections;

        public QuickServiceMenuAdapter(ArrayList<Section> sections) {
            mSections = sections;
        }

        @Override
        public QuickServiceMenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return null;
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quick_service_menu_layout, parent, false);
            return new QuickServiceMenuHolder(view);
        }

        @Override
        public void onBindViewHolder(QuickServiceMenuHolder holder, int position) {
            final Section section = mSections.get(position);
            holder.sectionHeaderName.setText(section.getSectionName());
            holder.sectionHeaderName.setTypeface(TypefaceUtil.getBold(getActivity()));
            holder.sectionHeaderCard.setClickable(false);

            ArrayList<SectionItem> sectionItems = section.getSectionItems();
            LinearLayout layout = holder.sectionMenuList;
            if (layout == null) {
                Log.e(TAG, "onBindViewHolder linear layout is null");
            }
            layout.setVisibility(View.VISIBLE);
            layout.removeAllViews();
            layout.removeAllViewsInLayout();

            if (sectionItems == null || sectionItems.isEmpty()) {
                holder.sectionMenuCard.setVisibility(View.GONE);
            } else {
                holder.sectionMenuCard.setVisibility(View.VISIBLE);
                for (SectionItem item : sectionItems) {
                    if (item instanceof RateableItem) {
                        final RateableItem rateableItem = (RateableItem) item;
                        View rootView = getActivity().getLayoutInflater().inflate(R.layout.food_item_card_layout, null, false);
                        rootView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Toast.makeText(getActivity(), rateableItem.getItemName() + " clicked.", Toast.LENGTH_SHORT).show();
                                // open the right nutrition pop up
                                showSwipeRefresh();
                                FoodItemUtils.openInfoPopupForFoodItem(rateableItem, getActivity(), new OnCompleteListener() {
                                    @Override
                                    public void onComplete() {
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

                        rootView.findViewById(R.id.food_item_right_button).setVisibility(View.VISIBLE);

                        ViewParent parent = rootView.getParent();
                        if (parent != null) {
                            ((ViewGroup) parent).removeView(rootView);
                        }
                        layout.addView(rootView);
                    } else if (item instanceof SectionInfoItem) {
                        SectionInfoItem infoItem = (SectionInfoItem) item;
                        View rootView = getActivity().getLayoutInflater().inflate(R.layout.food_item_card_layout, null, false);
                        rootView.setClickable(false);

                        TextView titletext = ((TextView) rootView.findViewById(R.id.food_item_name));
                        titletext.setText(infoItem.getInfoText());
                        titletext.setTypeface(TypefaceUtil.getRegular(getActivity()));

                        (rootView.findViewById(R.id.food_item_subtitle)).setVisibility(View.GONE);

                        rootView.findViewById(R.id.food_item_right_button).setVisibility(View.GONE);

                        if (infoItem.getInfoText() == null || infoItem.getInfoText().isEmpty()) {
                            rootView.setVisibility(View.GONE);
                            // TODO: look into this
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
