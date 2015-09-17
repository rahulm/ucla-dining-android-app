package com.maninbrown.ucladining.util;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.maninbrown.ucladining.R;

import api.DiningAPI;
import models.BaseModel;
import models.FoodItemInfo;
import models.RateableItem;
import util.diningAPICallbacks.OnCompleteListener;
import util.diningAPICallbacks.OnFailureListener;
import util.diningAPICallbacks.OnSuccessListener;

/**
 * Created by Rahul on 9/15/2015.
 */
public class FoodItemUtils {
    private static final String TAG = "FoodItemUtils";

    private static void logDebug(String message) {
        DebugUtils.logDebug(TAG, message);
    }


    public static boolean popUpWindowIsShowing = false;

    private static PopupWindow mPopUpWindow;

    private static boolean isConnecting = false;

    private static void doFailure(OnCompleteListener onCompleteListener, OnFailureListener onFailureListener) {
        if (onCompleteListener != null)
            onCompleteListener.onComplete();

        if (onFailureListener != null)
            onFailureListener.onFailure();
    }

    private static void doSuccess(BaseModel baseModel, OnCompleteListener onCompleteListener, OnSuccessListener onSuccessListener) {
        if (onCompleteListener != null)
            onCompleteListener.onComplete();

        if (onSuccessListener != null)
            onSuccessListener.onSuccess(baseModel);
    }

    public static void openInfoPopupForFoodItem(final RateableItem rateableItem, final Activity activity,
                                                final OnCompleteListener onCompleteListener,
                                                final OnSuccessListener onSuccessListener,
                                                final OnFailureListener onFailureListener) {
        if (isConnecting) {
            return;
        }

        isConnecting = true;
        if (rateableItem == null || activity == null) {
            Log.e(TAG, "openInfoPopupForFoodItem rateable item or context is null!");
            Toast.makeText(activity, "Sorry, something went wrong! Please try again later.", Toast.LENGTH_SHORT).show();
            doFailure(onCompleteListener, onFailureListener);
        } else {
            logDebug("food item info url " + rateableItem.getTargetURL());
            DiningAPI.getNutritionInfo(rateableItem, new OnCompleteListener() {
                @Override
                public void onComplete() {
                    isConnecting = false;
                }
            }, new OnSuccessListener() {
                @Override
                public void onSuccess(BaseModel baseModel) {
                    showFoodItemInfoPopup((FoodItemInfo) baseModel, activity);
                    doSuccess(baseModel, onCompleteListener, onSuccessListener);
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure() {
                    FoodItemInfo itemInfo = new FoodItemInfo(rateableItem, null, null);
                    showFoodItemInfoPopup(itemInfo, activity);
                    doSuccess(itemInfo, onCompleteListener, onSuccessListener);
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(activity, "Uh oh, something went wrong! Please try again later.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    doFailure(onCompleteListener, onFailureListener);
                }
            });
        }
    }

    private static void showFoodItemInfoPopup(final FoodItemInfo foodItemInfo, final Activity activity) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RateableItem rateableItem = foodItemInfo.getFoodItem();

                View rootView = activity.getLayoutInflater().inflate(R.layout.food_item_info_layout, null);

                TextView textView = (TextView) rootView.findViewById(R.id.food_item_info_name);
                textView.setText(rateableItem.getItemName());
                textView.setTypeface(TypefaceUtil.getBold(activity));

                textView = (TextView) rootView.findViewById(R.id.food_item_info_details);
                String details = rateableItem.getItemDescription();
                if (details != null && !details.isEmpty()) {
                    textView.setText(details);
                    textView.setVisibility(View.VISIBLE);
                    textView.setTypeface(TypefaceUtil.getItalic(activity));
                } else {
                    textView.setVisibility(View.GONE);
                }

                CardView cardView = (CardView) rootView.findViewById(R.id.food_item_info_ingredients_card);
                textView = (TextView) cardView.findViewById(R.id.food_item_info_ingredients_text);
                String ingredients = foodItemInfo.getIngredientsList();
                if (ingredients != null && !ingredients.isEmpty()) {
                    textView.setText(ingredients);
                    textView.setTypeface(TypefaceUtil.getItalic(activity));
                    cardView.setVisibility(View.VISIBLE);
                } else {
                    textView.setText("No ingredient info to display :(");
                    textView.setTypeface(TypefaceUtil.getItalic(activity));
                    cardView.setVisibility(View.VISIBLE);
                }

                WebView webView = (WebView) rootView.findViewById(R.id.food_item_info_nutrition_view);
                String nutritionHTML = foodItemInfo.getNutritionFactsHTML();
                if (nutritionHTML!=null && !nutritionHTML.isEmpty()) {
                    webView.setVisibility(View.VISIBLE);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadData(foodItemInfo.getNutritionFactsHTML(), "text/html", "UTF-8");
                    logDebug("webview html: " + foodItemInfo.getNutritionFactsHTML());
                } else {
                    webView.setVisibility(View.GONE);
                }

                mPopUpWindow = new PopupWindow(rootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                mPopUpWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        popUpWindowIsShowing = false;
                    }
                });
                mPopUpWindow.setTouchable(true);

                View space = (rootView.findViewById(R.id.food_item_info_background_space));
                space.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPopUpWindow.isShowing()) mPopUpWindow.dismiss();
                    }
                });
                space.setClickable(true);


                mPopUpWindow.setContentView(rootView);
//                mPopUpWindow.setOutsideTouchable(true);
//                mPopUpWindow.setFocusable(true);

                mPopUpWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                popUpWindowIsShowing = true;
            }
        });
    }

    public static void dismissPopUp() {
        if (mPopUpWindow != null && mPopUpWindow.isShowing()) {
            mPopUpWindow.dismiss();
            popUpWindowIsShowing = false;
        }
    }
}
