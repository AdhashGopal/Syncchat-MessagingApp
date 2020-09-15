package com.chatapp.android.app.utils;

import android.content.Context;

import com.chatapp.android.core.SessionManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * created by  Adhash Team on 5/12/2017.
 */
public class TimeStampUtils {

    /**
     * get 12Hr TimeFormat
     *
     * @param context current activity
     * @param ts      timestamp
     * @return value
     */
    public static String get12HrTimeFormat(Context context, String ts) {

        try {
            long givenTS = Long.parseLong(ts);
            long serverDiff = SessionManager.getInstance(context).getServerTimeDifference();

            long deviceTS = givenTS + serverDiff;
            Date dateObj = new Date(deviceTS);
            String timeStamp = new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(dateObj);
            return timeStamp;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * get Message timestamp to Date
     *
     * @param context current activity
     * @param ts      timestamp
     * @return value
     */
    public static Date getMessageTStoDate(Context context, String ts) {
        try {
            long givenTS = Long.parseLong(ts);
            long serverDiff = SessionManager.getInstance(context).getServerTimeDifference();
            long deviceTS = givenTS + serverDiff;

            DateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date dateTemp = new Date(deviceTS);
            return df.parse(df.format(dateTemp));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get Date Format
     *
     * @param ts timestamp
     * @return value
     */
    public static Date getDateFormat(long ts) {
        try {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date dateTemp = new Date(ts);
            return df.parse(df.format(dateTemp));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get Date
     *
     * @param ts timestamp
     * @return value
     */
    public static String getDate(long ts) {
        try {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date dateTemp = new Date(ts);
            return df.format(dateTemp);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * get Yesterday Date
     *
     * @param today check current date
     * @return value
     */
    public static Date getYesterdayDate(Date today) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();
        return yesterday;
    }

    /**
     * get Server Time Stamp
     *
     * @param context  current activity
     * @param deviceTS device time
     * @return value
     */
    public static String getServerTimeStamp(Context context, long deviceTS) {
        long serverDiff = SessionManager.getInstance(context).getServerTimeDifference();
        long serverTS = deviceTS + serverDiff;
        return String.valueOf(serverTS);
    }

    /**
     * addDay
     *
     * @param date  current date
     * @param count base on input
     * @return value
     */
    public static Date addDay(Date date, int count) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, count);
        return cal.getTime();
    }

    /**
     * addHour
     *
     * @param date  current date
     * @param count base on input
     * @return value
     */
    public static Date addHour(Date date, int count) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, count);
        return cal.getTime();
    }

    /**
     * addYear
     *
     * @param date  current date
     * @param count base on input
     * @return value
     */
    public static Date addYear(Date date, int count) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, count);
        return cal.getTime();
    }
}
