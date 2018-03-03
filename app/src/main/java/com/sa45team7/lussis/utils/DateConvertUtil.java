package com.sa45team7.lussis.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by nhatton on 1/20/18.
 * Util class to deal with converting date to string
 */

public class DateConvertUtil {

    private static final SimpleDateFormat formatFromServer = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    private static final SimpleDateFormat formatForRequisitions = new SimpleDateFormat("MMM d", Locale.US);
    private static final SimpleDateFormat formatForDetailDate = new SimpleDateFormat("EEE, d MMM", Locale.US);

    static {
        formatFromServer.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Method to convert to type MMM d
     * @param date date to be formatted
     * @return formatted date in string
     */
    public static String convertForRequisitions(Date date) {
        return formatForRequisitions.format(date);
    }

    /**
     * Method to convert to type EEE, D MMM
     * @param date date to be formatted
     * @return formatted date in string
     */
    public static String convertForDetail(Date date) {
        return formatForDetailDate.format(date);
    }

}
