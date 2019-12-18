package com.example.mobdev_project.Helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelpers {
    /**
     * Formats the date to dd/MM/yyyy.
     * @param date The date to format.
     * @return String in dd/MM/yyyy format.
     */
    public static String FormatDate(Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormatter.format(date);
    }

    /**
     * Formats a date to dd/MM/yyyy hh:mm
     * @param dateTime The date to format.
     * @return String in dd/MM/yyyy hh:mm
     */
    public static  String FormatDateTime(Date dateTime) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
        return dateFormatter.format(dateTime);
    }
}
