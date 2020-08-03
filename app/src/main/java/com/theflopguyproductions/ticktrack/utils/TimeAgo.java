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

    public static String getTimerTitle(int hour, int minute, int second){

        String perfectHour = hour+"";
        String perfectMinute = minute+"";
        String perfectSecond = second+"";

        if(hour<10){ perfectHour = appendHour(hour); }
        if(minute<10){ perfectMinute = appendMinute(minute); }
        if(second<10){ perfectSecond = appendSecond(second); }

        if(hour>0){
            if(hour==1){
                if(minute==1){
                    if(second==1){
                        return perfectHour+"hr "+perfectMinute+"min "+perfectSecond+"sec";
                    } else if(second==0){
                        return perfectHour+"hr "+perfectMinute+"min ";
                    } else {
                        return perfectHour+"hr "+perfectMinute+"min "+perfectSecond+"secs";
                    }
                } else if(minute==0) {
                    if(second==1){
                        return perfectHour+"hr "+perfectSecond+"sec";
                    } else if(second==0){
                        return perfectHour+"hr ";
                    } else {
                        return perfectHour+"hr "+perfectSecond+"secs";
                    }
                }
                else {
                    if(second==1){
                        return perfectHour+"hr "+perfectMinute+"mins "+perfectSecond+"sec";
                    } else if(second==0){
                        return perfectHour+"hr "+perfectMinute+"mins ";
                    } else {
                        return perfectHour+"hr "+perfectMinute+"mins "+perfectSecond+"secs";
                    }
                }
            } else {
                if(minute==1){
                    if(second==1){
                        return perfectHour+"hrs "+perfectMinute+"min "+perfectSecond+"sec";
                    } else if(second==0){
                        return perfectHour+"hrs "+perfectMinute+"min ";
                    } else {
                        return perfectHour+"hrs "+perfectMinute+"min "+perfectSecond+"secs";
                    }
                } else if(minute==0) {
                    if(second==1){
                        return perfectHour+"hrs "+perfectSecond+"sec";
                    } else if(second==0){
                        return perfectHour+"hrs ";
                    } else {
                        return perfectHour+"hrs "+perfectSecond+"secs";
                    }
                }
                else {
                    if(second==1){
                        return perfectHour+"hrs "+perfectMinute+"mins "+perfectSecond+"sec";
                    } else if(second==0){
                        return perfectHour+"hrs "+perfectMinute+"mins ";
                    } else {
                        return perfectHour+"hrs "+perfectMinute+"mins "+perfectSecond+"secs";
                    }
                }
            }
        } else if(hour==0) {
            if(minute>0){
                if(minute==1){
                    if(second==1){
                        return perfectMinute+"min "+perfectSecond+"sec";
                    } else if(second==0){
                        return perfectMinute+"min ";
                    } else {
                        return perfectMinute+"min "+perfectSecond+"secs";
                    }
                } else {
                    if(second==1){
                        return perfectMinute+"mins "+perfectSecond+"sec";
                    } else if(second==0){
                        return perfectMinute+"mins ";
                    } else {
                        return perfectMinute+"mins "+perfectSecond+"secs";
                    }
                }
            } else if(minute==0){
                if(second>0){
                    if(second==1){
                        return perfectSecond+"sec";
                    } else {
                        return perfectSecond+"secs";
                    }
                } else {
                    return "Invalid timer";
                }
            } else {
                return "Invalid timer";
            }
        } else {
            return "Invalid timer";
        }
    }

    private static String appendSecond(int second) {
        if(second>10){
            return second+"";
        } else {
            return "0"+second;
        }
    }

    private static String appendMinute(int minute) {
        if(minute>10){
            return minute+"";
        } else {
            return "0"+minute;
        }
    }

    private static String appendHour(int hour) {
        if(hour>10){
            return hour+"";
        } else {
            return "0"+hour;
        }
    }

    public static long getTimerDataInMillis(int pickedHour, int pickedMinute, int pickedSecond, int pickedMilliSeconds) {
        long resultMillis = 0l;

        if(pickedHour>0){
            resultMillis += pickedHour*60*60*1000;
        }
        if(pickedMinute>0){
            resultMillis += pickedMinute*60*1000;
        }
        if(pickedSecond>0){
            resultMillis += pickedSecond*1000;
        }
        if(pickedMilliSeconds>0){
            resultMillis += pickedMilliSeconds;
        }

        return resultMillis;
    }

    public static String getTimerDurationLeft(int hourLeft, int minuteLeft, int secondLeft){

        if(hourLeft>1){
            if(minuteLeft>1){
                return "less than "+hourLeft+" hours"+minuteLeft+" minutes remaining";
            } else if(minuteLeft==0){
                return "less than "+hourLeft+" hours remaining";
            } else if(minuteLeft==1){
                return "less than "+hourLeft+" hours"+minuteLeft+" minute remaining";
            } else {
                return "less than "+hourLeft+" hours"+secondLeft+" seconds remaining";
            }
        } else if(hourLeft==0){
            if(minuteLeft>1){
                return "less than "+minuteLeft+" minutes remaining";

            } else if(minuteLeft==0){
                return "less than a minute remaining";

            } else if(minuteLeft==1){
                return "less than a minute remaining";
            } else {
                return "less than a minute remaining";
            }
        } else if(hourLeft==1) {
            if(minuteLeft>1){
                return "less than "+hourLeft+" hour"+minuteLeft+" minutes remaining";
            } else if(minuteLeft==0){
                return "less than "+hourLeft+" hour remaining";
            } else if(minuteLeft==1){
                return "less than "+hourLeft+" hour"+minuteLeft+" minute remaining";
            } else {
                return hourLeft+" hour remaining";
            }
        } else {
            return "a few seconds left";
        }
    }
}
