package com.chatapp.synchat.core.chatapphelperclass;


import java.util.Date;

import java.util.Locale;

import java.util.TimeZone;

import java.text.ParseException;


import java.text.SimpleDateFormat;


/**
 */
public class ChatappUtilities {


    public String gmtToEpoch(String tsingmt) {


        Date d = null;
        String s = null;
        long epoch = 0;


        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS z");

        try {
            d = formater.parse(tsingmt);

            epoch = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return String.valueOf(epoch);
    }


    public static long Daybetween(String date1, String date2, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        Date Date1 = null, Date2 = null;
        try {
            Date1 = sdf.parse(date1);
            Date2 = sdf.parse(date2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (Date2.getTime() - Date1.getTime()) / (24 * 60 * 60 * 1000);
    }


    public static String formatDate(String ts) {

        String s = null;
        Date d = null;


        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS z");

        try {
            d = formater.parse(ts);

            formater = new SimpleDateFormat("HH:mm:ss EEE dd/MMM/yyyy z");
            s = formater.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return s;

    }


    public static String changeStatusDateFromGMTToLocal(String ts) {


        String s = null;
        Date d = null;


        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

        try {
            d = formater.parse(ts);

            TimeZone tz = TimeZone.getDefault();
            formater.setTimeZone(tz);

            s = formater.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return s;
    }


    public static String epochtoGmt(String tsingmt) {


        Date d = null;
        String s = null;
        long epoch = 0;


        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS z");
        formater.setTimeZone(TimeZone.getTimeZone("GMT"));
        epoch = Long.parseLong(tsingmt);

        d = new Date(epoch);
        s = formater.format(d);


        return s;
    }


    public static String epochtoGmtShort(String tsingmt) {
        Date d = null;
        String s = null;
        long epoch = 0;
        final SimpleDateFormat formater = new SimpleDateFormat("H:mm");
//        formater.setTimeZone(TimeZone.getTimeZone("GMT"));
        epoch = Long.parseLong(tsingmt);
        d = new Date(epoch);
        s = formater.format(d);
        return s;
    }


    public static String docIDepochtoGmtShort(String[] docID) {
        String tsingmt = docID[2];
        Date d = null;
        String s = null;
        long epoch = 0;
        final SimpleDateFormat formater = new SimpleDateFormat("H:mm");
        formater.setTimeZone(TimeZone.getTimeZone("GMT"));
        epoch = Long.parseLong(tsingmt);
        d = new Date(epoch);
        s = formater.format(d);
        return s;
    }

    public static String convert24to12hourformat(String d) {

        String datein12hour = null;

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(d);
            System.out.println(dateObj);
            System.out.println(new SimpleDateFormat("h:mm a").format(dateObj));
            datein12hour = new SimpleDateFormat("h:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }


        return datein12hour;

    }

    /*public static Date convertTStoDate(long ts) {

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            Date dateTemp = new Date(ts);
            Date date = df.parse(df.format(dateTemp));
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }*/

    public static String tsInGmt() {


        Date localTime = new Date();


        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS z");
        formater.setTimeZone(TimeZone.getTimeZone("GMT"));


        String s = formater.format(localTime);
        return s;
    }


    //converting tsNextLine to localtime zone from gmt tsNextLine

    public static String tsFromGmt(String tsingmt) {
        Date d = null;
        String s = null;
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmssSSS z");
        try {
            d = formater.parse(tsingmt);
            TimeZone tz = TimeZone.getDefault();
            formater.setTimeZone(tz);
            s = formater.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }

}
