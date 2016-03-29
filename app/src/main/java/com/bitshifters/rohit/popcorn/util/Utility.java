package com.bitshifters.rohit.popcorn.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rohit on 29/3/16.
 */
public class Utility {
    private final static String TAG = Utility.class.getSimpleName();

    public static String getFormattedDate(String dateString ) {
        String inputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.US);
        try {
            Date date = inputFormat.parse(dateString);
            return DateFormat.getDateInstance().format(date);
        } catch (ParseException e) {
            Log.e(TAG, "The Release data was not parsed successfully: " + dateString);
            // Return not formatted date
        }
        return dateString;
    }


}
