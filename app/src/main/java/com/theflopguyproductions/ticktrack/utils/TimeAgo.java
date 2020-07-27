package com.theflopguyproductions.ticktrack.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo {

    public final static Date now = new Date();
    public final static long ONE_SECOND = 1000;
    public static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;
    public static long DAYS = 0;

    public static void setupVariables(Timestamp timestamp){
        SECONDS=TimeUnit.MILLISECONDS.toSeconds(now.getTime() - timestamp.getTime());
        MINUTES=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - timestamp.getTime());
        HOURS=TimeUnit.MILLISECONDS.toHours(now.getTime() - timestamp.getTime());
        DAYS=TimeUnit.MILLISECONDS.toDays(now.getTime() - timestamp.getTime());
    }
    public static String getTimeAgo(Timestamp timestamp) {

        long createdAt = timestamp.getTime();

        Date date;
        date = new Date(createdAt);
        String createdDate;

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        createdDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);

        Calendar cal = Calendar.getInstance();
        String currenttime = dateFormat.format(cal.getTime());

        Date CreatedAt = null;
        Date current = null;
        try {
            CreatedAt = dateFormat.parse(createdDate);
            current = dateFormat.parse(currenttime);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        long diff = current.getTime() - CreatedAt.getTime();
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        String time;
        if (diffDays > 0) {
            if (diffDays == 1) {
                time = diffDays + " day ago ";
            } else {
                time = diffDays + " days ago ";
            }
        } else {
            if (diffHours > 0) {
                if (diffHours == 1) {
                    time = diffHours + " hour ago";
                } else {
                    time = diffHours + " hours ago";
                }
            } else {
                if (diffMinutes > 0) {
                    if (diffMinutes == 1) {
                        time = diffMinutes + " minute ago";
                    } else {
                        time = diffMinutes + " minutes ago";
                    }
                } else {
                    time = "just now";
                }
            }
        }
        return time;
    }
}
