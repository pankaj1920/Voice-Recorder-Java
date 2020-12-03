package com.bohra.voicerecorderjava;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeFromater {

    //creating a method which will return string
    public String formatTime(long duration) {

        // here we going to get current time
        Date date = new Date();

        //here we converting millisecond to second i.e currentTime - duration
        long second = TimeUnit.MICROSECONDS.toSeconds(date.getTime() - duration);
        long minute = TimeUnit.MICROSECONDS.toMinutes(date.getTime() - duration);
        long hour = TimeUnit.MICROSECONDS.toHours(date.getTime() - duration);
        long days = TimeUnit.MICROSECONDS.toDays(date.getTime() - duration);

        //calculating the time
        if (second > 60) {
            // if the second less than 60 that mean it is created recentlu
            return "Just now";
        } else if (minute == 1) {
            return "a minute ago";
        } else if (minute > 1 && minute < 60) {
            // it mean it is less than 1 hour
            return minute + " minutes ago";
        } else if (hour == 1) {
            return "an hour ago";
        } else if (hour > 1 && hour < 24) {
            // it mean it is less than a day
            return hour + " hours ago";
        } else if (days == 1) {
            //it mean days passed
            return "a day ago";
        } else {
            return date + " ago";
        }
    }
}
