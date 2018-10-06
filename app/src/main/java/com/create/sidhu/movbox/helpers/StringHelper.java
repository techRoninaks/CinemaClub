package com.create.sidhu.movbox.helpers;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * String formatting helper
 */

public class StringHelper {
    /***
     * Formats Integers. Eg: 20,000 to 20k. 43,744 to 43.74k
     * @param count - The integer to format
     * @return - Returns the formatted string
     */
    public static String formatTextCount(int count){
        String formattedCount = "";
        if(count >= 10000){
            if(count % 1000 == 0){
                formattedCount = "" + count/1000 + "k";
            }else {
                double temp = ((double) count) / 1000.0;
                formattedCount = "" + (Math.round(temp * 100.0) % 100 == 0 ? Math.round(temp) : Math.round(temp * 100.0)/ 100.0) + "k";
            }
        }
        else
            formattedCount = "" + count;
        return formattedCount;
    }

    /***
     * Converts string to Sentence case
     * @param s- String to be converted
     * @return Sentence cased string
     */
    public static String toSentenceCase(String s){
        if(s != null && !s.isEmpty())
            return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
        else
            return "";
    }

    /***
     * Converts string to Title case
     * @param s- String to be converted
     * @return Title cased string
     */
    public static String toTitleCase(String s){
        if(s != null && !s.isEmpty()) {
            int length = s.length();
            String str = "";
            String strTemp = "";
            for (int i = 0; i < length; i++) {
                char ch = s.charAt(i);
                if (ch == ' ') {
                    strTemp += ch;
                    str = str.concat(toSentenceCase(strTemp));
                    strTemp = "";
                } else {
                    strTemp += ch;
                }
            }
            str = str.concat(toSentenceCase(strTemp));
            return str;
        }else
            return "";
    }

    public static void changeToolbarFont(Toolbar toolbar, Activity context) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    applyFont(tv, context);
                    break;
                }
            }
        }
    }

    public static void applyFont(TextView tv, Activity context) {
        tv.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Semibold.otf"));
    }

    public static Date getDate(String datetime, String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        try {
            Date date = formatter.parse(datetime);
            return date;
        }catch (Exception e){
            Log.e("StringHelper:getDate", e.getMessage());
        }
        try {
            return formatter.parse(formatter.format(Calendar.getInstance().getTime()));
        } catch (ParseException e) {
            Log.e("StringHelper:getDate", e.getMessage());
        }
        return new Date();
    }
}
