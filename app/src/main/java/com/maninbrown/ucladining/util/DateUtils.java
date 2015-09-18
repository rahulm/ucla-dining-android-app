package com.maninbrown.ucladining.util;

import org.joda.time.DateTime;

import java.util.HashMap;

import util.NetworkHelpers;

/**
 * Created by Rahul on 9/18/2015.
 */
public class DateUtils {

    public static String getDateStringFromDateTime(DateTime dateTime) {
        String dateString = "";

        if (dateTime == null) {
            return "";
        }
        dateString = NetworkHelpers.getDateString(dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), dateTime.getYear());
        return dateString;
    }
}
